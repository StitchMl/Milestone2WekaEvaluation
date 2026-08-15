/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.generic;
import java.nio.ByteBuffer;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.Schema.Type;
import org.apache.avro.UnresolvedUnionException;
import org.apache.avro.io.BinaryData;
import org.apache.avro.util.Utf8;
/** Utilities for generic Java data. */
public class GenericData {
  private static final GenericData INSTANCE = new GenericData();
  /** Return the singleton instance. */
  public static GenericData get() { return INSTANCE; }
  protected GenericData() {}
  /** Default implementation of {@link GenericRecord}. */
  public static class Record implements GenericRecord, Comparable<Record> {
    private final Schema schema;
    private final Object[] values;
    public Record(Schema schema) {
      if (schema == null || !Type.RECORD.equals(schema.getType()))
        throw new AvroRuntimeException("Not a record schema: " + schema);
      this.schema = schema;
      this.values = new Object[schema.getFields().size()];
    }
    @Override public Schema getSchema() { return schema; }
    @Override public void put(String key, Object value) {
      values[schema.getField(key).pos()] = value;
    }
    @Override public void put(int i, Object v) { values[i] = v; }
    @Override public Object get(String key) {
      Field field = schema.getField(key);
      if (field == null) return null;
      return values[field.pos()];
    }
    @Override public Object get(int i) { return values[i]; }
    @Override public boolean equals(Object o) {
      if (o == this) return true;                 // identical object
      if (!(o instanceof Record)) return false;   // not a record
      Record that = (Record) o;
      if (!schema.getFullName().equals(that.schema.getFullName()))
        return false;                             // not the same schema
      return GenericData.get().compare(this, that, schema, true) == 0;
    }
    @Override public int hashCode() {
      return GenericData.get().hashCode(this, schema);
    }
    @Override public int compareTo(Record that) {
      return GenericData.get().compare(this, that, schema);
    }
    @Override public String toString() {
      return GenericData.get().toString(this);
    }
  }
  /** Default implementation of an array. */
  @SuppressWarnings(value = "unchecked")
  public static class Array<T> extends AbstractList<T>
      implements GenericArray<T>, Comparable<GenericArray<T>> {
    private static final Object[] EMPTY = new Object[0];
    private final Schema schema;
    private int size;
    private Object[] elements = EMPTY;
    public Array(int capacity, Schema schema) {
      if (schema == null || !Type.ARRAY.equals(schema.getType()))
        throw new AvroRuntimeException("Not an array schema: " + schema);
      this.schema = schema;
      if (capacity != 0)
        elements = new Object[capacity];
    }
    public Schema getSchema() { return schema; }
    @Override public int size() { return size; }
    @Override public void clear() { size = 0; }
    @Override public Iterator<T> iterator() {
      return new Iterator<T>() {
        private int position = 0;
        public boolean hasNext() { return position < size; }
        public T next() { return (T) elements[position++]; }
        public void remove() { throw new UnsupportedOperationException(); }
      };
    }
    @Override public T get(int i) {
      if (i >= size)
        throw new IndexOutOfBoundsException("Index " + i + " out of bounds.");
      return (T) elements[i];
    }
    @Override public boolean add(T o) {
      if (size == elements.length) {
        Object[] newElements = new Object[(size * 3) / 2 + 1];
        System.arraycopy(elements, 0, newElements, 0, size);
        elements = newElements;
      }
      elements[size++] = o;
      return true;
    }
    @Override public T set(int i, T o) {
      if (i >= size)
        throw new IndexOutOfBoundsException("Index " + i + " out of bounds.");
      T response = (T) elements[i];
      elements[i] = o;
      return response;
    }
    @Override public T remove(int i) {
      if (i >= size)
        throw new IndexOutOfBoundsException("Index " + i + " out of bounds.");
      T result = (T) elements[i];
      --size;
      System.arraycopy(elements, i + 1, elements, i, (size - i));
      elements[size] = null;
      return result;
    }
    public T peek() {
      return (size < elements.length) ? (T) elements[size] : null;
    }
    @Override
    public int hashCode() {
      return GenericData.get().hashCode(this, schema);
    }
    @Override
    public boolean equals(Object o) {
      if (o == this) return true;                 // identical object
      if (!(o instanceof Array)) return false;    // not an array
      Array that = (Array) o;
      if (!schema.equals(that.schema))
        return false;                             // not the same schema
      return this.compareTo(that) == 0;
    }
    @Override
    public int compareTo(GenericArray<T> that) {
      return GenericData.get().compare(this, that, this.getSchema());
    }
    public void reverse() {
      int left = 0;
      int right = elements.length - 1;
      while (left < right) {
        Object tmp = elements[left];
        elements[left] = elements[right];
        elements[right] = tmp;
        left++;
        right--;
      }
    }
    @Override
    public String toString() {
      StringBuffer buffer = new StringBuffer();
      buffer.append("[");
      int count = 0;
      for (T e : this) {
        buffer.append(e == null ? "null" : e.toString());
        if (++count < size())
          buffer.append(", ");
      }
      buffer.append("]");
      return buffer.toString();
    }
  }
  /** Default implementation of {@link GenericFixed}. */
  public static class Fixed implements GenericFixed, Comparable<Fixed> {
    private Schema schema;
    private byte[] bytes;
    public Fixed(Schema schema) { setSchema(schema); }
    public Fixed(Schema schema, byte[] bytes) {
      this.schema = schema;
      this.bytes = bytes;
    }
    protected Fixed() {}
    protected void setSchema(Schema schema) {
      this.schema = schema;
      this.bytes = new byte[schema.getFixedSize()];
    }
    @Override public Schema getSchema() { return schema; }
    public void bytes(byte[] bytes) { this.bytes = bytes; }
    public byte[] bytes() { return bytes; }
    @Override
    public boolean equals(Object o) {
      if (o == this) return true;
      return o instanceof GenericFixed
          && Arrays.equals(bytes, ((GenericFixed) o).bytes());
    }
    @Override
    public int hashCode() { return Arrays.hashCode(bytes); }
    @Override
    public String toString() { return Arrays.toString(bytes); }
    @Override
    public int compareTo(Fixed that) {
      return BinaryData.compareBytes(this.bytes, 0, this.bytes.length,
                                    that.bytes, 0, that.bytes.length);
    }
  }
  /** Default implementation of {@link GenericEnumSymbol}. */
  public static class EnumSymbol implements GenericEnumSymbol {
    private Schema schema;
    private String symbol;
    public EnumSymbol(Schema schema, String symbol) {
      this.schema = schema;
      this.symbol = symbol;
    }
    @Override public Schema getSchema() { return schema; }
    @Override
    public boolean equals(Object o) {
      if (o == this) return true;
      return o instanceof GenericEnumSymbol
          && symbol.equals(o.toString());
    }
    @Override
    public int hashCode() { return symbol.hashCode(); }
    @Override
    public String toString() { return symbol; }
  }
  /** Returns true if a Java datum matches a schema. */
  public boolean validate(Schema schema, Object datum) {
    switch (schema.getType()) {
    case RECORD:
      return validateRecord(schema, datum);
    case ENUM:
      return validateEnum(schema, datum);
    case ARRAY:
      return validateArray(schema, datum);
    case MAP:
      return validateMap(schema, datum);
    case UNION:
      return validateUnion(schema, datum);
    case FIXED:
      return validateFixed(schema, datum);
    case STRING:
      return isString(datum);
    case BYTES:
      return isBytes(datum);
    case INT:
      return datum instanceof Integer;
    case LONG:
      return datum instanceof Long;
    case FLOAT:
      return datum instanceof Float;
    case DOUBLE:
      return datum instanceof Double;
    case BOOLEAN:
      return datum instanceof Boolean;
    case NULL:
      return datum == null;
    default:
      return false;
    }
  }
  private boolean validateRecord(Schema schema, Object datum) {
    if (!(datum instanceof IndexedRecord)) {
      return false;
    }
    IndexedRecord record = (IndexedRecord) datum;
    for (Field field : schema.getFields()) {
      if (!validate(field.schema(), record.get(field.pos()))) {
        return false;
      }
    }
    return true;
  }
  private boolean validateEnum(Schema schema, Object datum) {
    return schema.getEnumSymbols().contains(datum.toString());
  }
  private boolean validateArray(Schema schema, Object datum) {
    if (!(datum instanceof Collection)) {
      return false;
    }
    for (Object element : (Collection<?>) datum) {
      if (!validate(schema.getElementType(), element)) {
        return false;
      }
    }
    return true;
  }
  @SuppressWarnings("unchecked")
  private boolean validateMap(Schema schema, Object datum) {
    if (!(datum instanceof Map)) {
      return false;
    }
    Map<Object, Object> map = (Map<Object, Object>) datum;
    for (Map.Entry<Object, Object> entry : map.entrySet()) {
      if (!validate(schema.getValueType(), entry.getValue())) {
        return false;
      }
    }
    return true;
  }
  private boolean validateUnion(Schema schema, Object datum) {
    for (Schema type : schema.getTypes()) {
      if (validate(type, datum)) {
        return true;
      }
    }
    return false;
  }
  private boolean validateFixed(Schema schema, Object datum) {
    return datum instanceof GenericFixed
        && ((GenericFixed) datum).bytes().length == schema.getFixedSize();
  }
  /** Renders a Java datum as <a href="http://www.json.org/">JSON</a>. */
  public String toString(Object datum) {
    StringBuilder buffer = new StringBuilder();
    toString(datum, buffer);
    return buffer.toString();
  }
  /** Renders a Java datum as <a href="http://www.json.org/">JSON</a>. */
  protected void toString(Object datum, StringBuilder buffer) {
    if (datum instanceof IndexedRecord) {
      appendRecordToString((IndexedRecord) datum, buffer);
      return;
    }
    if (datum instanceof Collection) {
      appendCollectionToString((Collection<?>) datum, buffer);
      return;
    }
    if (datum instanceof Map) {
      appendMapToString((Map<?, ?>) datum, buffer);
      return;
    }
    if (datum instanceof CharSequence || datum instanceof GenericEnumSymbol) {
      appendQuotedString(buffer, datum.toString());
      return;
    }
    if (datum instanceof ByteBuffer) {
      appendByteBufferToString(buffer, (ByteBuffer) datum);
      return;
    }
    buffer.append(datum);
  }
  private void appendRecordToString(IndexedRecord record, StringBuilder buffer) {
    buffer.append("{");
    int count = 0;
    for (Field field : record.getSchema().getFields()) {
      toString(field.name(), buffer);
      buffer.append(": ");
      toString(record.get(field.pos()), buffer);
      if (++count < record.getSchema().getFields().size()) {
        buffer.append(", ");
      }
    }
    buffer.append("}");
  }
  private void appendCollectionToString(Collection<?> array, StringBuilder buffer) {
    buffer.append("[");
    long last = array.size() - 1;
    int i = 0;
    for (Object element : array) {
      toString(element, buffer);
      if (i++ < last) {
        buffer.append(", ");
      }
    }
    buffer.append("]");
  }
  @SuppressWarnings("unchecked")
  private void appendMapToString(Map<?, ?> map, StringBuilder buffer) {
    buffer.append("{");
    int count = 0;
    for (Map.Entry<?, ?> entry : map.entrySet()) {
      toString(entry.getKey(), buffer);
      buffer.append(": ");
      toString(entry.getValue(), buffer);
      if (++count < map.size()) {
        buffer.append(", ");
      }
    }
    buffer.append("}");
  }
  private void appendQuotedString(StringBuilder buffer, String value) {
    buffer.append("\"");
    writeEscapedString(value, buffer);
    buffer.append("\"");
  }
  private void appendByteBufferToString(StringBuilder buffer, ByteBuffer bytes) {
    buffer.append("{\"bytes\": \"");
    for (int i = bytes.position(); i < bytes.limit(); i++) {
      buffer.append((char) bytes.get(i));
    }
    buffer.append("\"}");
  }
  /* Adapted from http://code.google.com/p/json-simple */
  private void writeEscapedString(String string, StringBuilder builder) {
    for (int i = 0; i < string.length(); i++) {
      char ch = string.charAt(i);
      if (isEscapedCharacter(ch)) {
        appendEscapedCharacter(builder, ch);
      } else if (requiresUnicodeEscape(ch)) {
        appendUnicodeEscape(builder, ch);
      } else {
        builder.append(ch);
      }
    }
  }
  private boolean isEscapedCharacter(char ch) {
    switch (ch) {
    case '"':
    case '\\':
    case '\b':
    case '\f':
    case '\n':
    case '\r':
    case '\t':
    case '/':
      return true;
    default:
      return false;
    }
  }
  private void appendEscapedCharacter(StringBuilder builder, char ch) {
    switch (ch) {
    case '"':
      builder.append("\\\"");
      break;
    case '\\':
      builder.append("\\\\");
      break;
    case '\b':
      builder.append("\\b");
      break;
    case '\f':
      builder.append("\\f");
      break;
    case '\n':
      builder.append("\\n");
      break;
    case '\r':
      builder.append("\\r");
      break;
    case '\t':
      builder.append("\\t");
      break;
    case '/':
      builder.append("\\/");
      break;
    default:
      break;
    }
  }
  private boolean requiresUnicodeEscape(char ch) {
    return (ch >= '\u0000' && ch <= '\u001F')
        || (ch >= '\u007F' && ch <= '\u009F')
        || (ch >= '\u2000' && ch <= '\u20FF');
  }
  private void appendUnicodeEscape(StringBuilder builder, char ch) {
    builder.append("\\u");
    String hexValue = Integer.toHexString(ch);
    for (int index = 0; index < 4 - hexValue.length(); index++) {
      builder.append('0');
    }
    builder.append(hexValue.toUpperCase());
  }
  /** Create a schema given an example datum. */
  public Schema induce(Object datum) {
    if (datum instanceof IndexedRecord) {
      return ((IndexedRecord) datum).getSchema();
    }
    if (datum instanceof Collection) {
      return induceCollection((Collection<?>) datum);
    }
    if (datum instanceof Map) {
      return induceMap((Map<?, ?>) datum);
    }
    if (datum instanceof GenericFixed) {
      return induceFixed((GenericFixed) datum);
    }
    if (datum instanceof CharSequence) {
      return Schema.create(Type.STRING);
    }
    if (datum instanceof ByteBuffer) {
      return Schema.create(Type.BYTES);
    }
    if (datum instanceof Integer) {
      return Schema.create(Type.INT);
    }
    if (datum instanceof Long) {
      return Schema.create(Type.LONG);
    }
    if (datum instanceof Float) {
      return Schema.create(Type.FLOAT);
    }
    if (datum instanceof Double) {
      return Schema.create(Type.DOUBLE);
    }
    if (datum instanceof Boolean) {
      return Schema.create(Type.BOOLEAN);
    }
    if (datum == null) {
      return Schema.create(Type.NULL);
    }
    throw new AvroTypeException("Can't create schema for: " + datum);
  }
  private Schema induceCollection(Collection<?> data) {
    Schema elementType = null;
    for (Object element : data) {
      Schema candidateType = induce(element);
      if (elementType == null) {
        elementType = candidateType;
      } else if (!elementType.equals(candidateType)) {
        throw new AvroTypeException("No mixed type arrays.");
      }
    }
    if (elementType == null) {
      throw new AvroTypeException("Empty array: " + data);
    }
    return Schema.createArray(elementType);
  }
  private Schema induceMap(Map<?, ?> data) {
    Schema valueType = null;
    for (Map.Entry<?, ?> entry : data.entrySet()) {
      Schema candidateType = induce(entry.getValue());
      if (valueType == null) {
        valueType = candidateType;
      } else if (!valueType.equals(candidateType)) {
        throw new AvroTypeException("No mixed type map values.");
      }
    }
    if (valueType == null) {
      throw new AvroTypeException("Empty map: " + data);
    }
    return Schema.createMap(valueType);
  }
  private Schema induceFixed(GenericFixed fixed) {
    return Schema.createFixed(null, null, null, fixed.bytes().length);
  }
  /** Called by {@link GenericDatumReader#readRecord} to set a record fields
   * value to a record instance.  The default implementation is for {@link
   * IndexedRecord}.*/
  public void setField(Object record, String fieldName, int position, Object o) {
    ((IndexedRecord) record).put(position, o);
  }
  /** Called by {@link GenericDatumReader#readRecord} to retrieve a record
   * field value from a reused instance.  The default implementation is for
   * {@link IndexedRecord}.*/
  public Object getField(Object record, String fieldName, int position) {
    return ((IndexedRecord) record).get(position);
  }
  /** Return the index for a datum within a union.  Implemented with {@link
   * #instanceOf(Schema,Object)}.*/
  public int resolveUnion(Schema union, Object datum) {
    int i = 0;
    for (Schema type : union.getTypes()) {
      if (instanceOf(type, datum))
        return i;
      i++;
    }
    throw new UnresolvedUnionException(union, datum);
  }
  /** Called by {@link #resolveUnion(Schema,Object)}.  May be overridden for
      alternate data representations.*/
  protected boolean instanceOf(Schema schema, Object datum) {
    switch (schema.getType()) {
    case RECORD:
      if (!isRecord(datum)) return false;
      return (schema.getFullName() == null)
          ? getRecordSchema(datum).getFullName() == null
          : schema.getFullName().equals(getRecordSchema(datum).getFullName());
    case ENUM:
      if (!isEnum(datum)) return false;
      return schema.getFullName().equals(getEnumSchema(datum).getFullName());
    case ARRAY:   return isArray(datum);
    case MAP:     return isMap(datum);
    case FIXED:
      if (!isFixed(datum)) return false;
      return schema.getFullName().equals(getFixedSchema(datum).getFullName());
    case STRING:  return isString(datum);
    case BYTES:   return isBytes(datum);
    case INT:     return datum instanceof Integer;
    case LONG:    return datum instanceof Long;
    case FLOAT:   return datum instanceof Float;
    case DOUBLE:  return datum instanceof Double;
    case BOOLEAN: return datum instanceof Boolean;
    case NULL:    return datum == null;
    default: throw new AvroRuntimeException("Unexpected type: " + schema);
    }
  }
  /** Called by the default implementation of {@link #instanceOf}.*/
  protected boolean isArray(Object datum) {
    return datum instanceof Collection;
  }
  /** Called by the default implementation of {@link #instanceOf}.*/
  protected boolean isRecord(Object datum) {
    return datum instanceof IndexedRecord;
  }
  /** Called to obtain the schema of a record.  By default calls
   * {GenericContainer#getSchema().  May be overridden for alternate record
   * representations. */
  protected Schema getRecordSchema(Object recordData) {
    return ((GenericContainer) recordData).getSchema();
  }
  /** Called by the default implementation of {@link #instanceOf}.*/
  protected boolean isEnum(Object datum) {
    return datum instanceof GenericEnumSymbol;
  }
  /** Called to obtain the schema of a enum.  By default calls
    * {GenericContainer#getSchema().  May be overridden for alternate enum
    * representations. */
  protected Schema getEnumSchema(Object enumSymbol) {
    return ((GenericContainer) enumSymbol).getSchema();
  }
  /** Called by the default implementation of {@link #instanceOf}.*/
  protected boolean isMap(Object datum) {
    return datum instanceof Map;
  }
  /** Called by the default implementation of {@link #instanceOf}.*/
  protected boolean isFixed(Object datum) {
    return datum instanceof GenericFixed;
  }
  /** Called to obtain the schema of a fixed.  By default calls
   * {GenericContainer#getSchema().  May be overridden for alternate fixed
   * representations. */
  protected Schema getFixedSchema(Object fixed) {
    return ((GenericContainer) fixed).getSchema();
  }
  /** Called by the default implementation of {@link #instanceOf}.*/
  protected boolean isString(Object datum) {
    return datum instanceof CharSequence;
  }
  /** Called by the default implementation of {@link #instanceOf}.*/
  protected boolean isBytes(Object datum) {
    return datum instanceof ByteBuffer;
  }
  /** Compute a hash code according to a schema, consistent with {@link
   * #compare(Object,Object,Schema)}. */
  public int hashCode(Object o, Schema s) {
    if (o == null) return 0;                      // incomplete datum
    int hashCode = 1;
    switch (s.getType()) {
    case RECORD:
      IndexedRecord r = (IndexedRecord) o;
      for (Field f : s.getFields()) {
        if (f.order() == Field.Order.IGNORE)
          continue;
        hashCode = hashCodeAdd(hashCode, r.get(f.pos()), f.schema());
      }
      return hashCode;
    case ARRAY:
      Collection<?> a = (Collection<?>) o;
      Schema elementType = s.getElementType();
      for (Object e : a)
        hashCode = hashCodeAdd(hashCode, e, elementType);
      return hashCode;
    case UNION:
      return hashCode(o, s.getTypes().get(resolveUnion(s, o)));
    case ENUM:
      return s.getEnumOrdinal(o.toString());
    case NULL:
      return 0;
    case STRING:
      return (o instanceof Utf8 ? o : new Utf8(o.toString())).hashCode();
    default:
      return o.hashCode();
    }
  }
  /** Add the hash code for an object into an accumulated hash code. */
  protected int hashCodeAdd(int hashCode, Object o, Schema s) {
    return 31 * hashCode + hashCode(o, s);
  }
  /** Compare objects according to their schema.  If equal, return zero.  If
   * greater-than, return 1, if less than return -1.  Order is consistent with
   * that of {@link BinaryData#compare(byte[], int, byte[], int, Schema)}.
   */
  public int compare(Object o1, Object o2, Schema s) {
    return compare(o1, o2, s, false);
  }
  /** Comparison implementation.  When equals is true, only checks for equality,
   * not for order. */
  @SuppressWarnings("unchecked")
  protected int compare(Object o1, Object o2, Schema s, boolean equals) {
    if (o1 == o2) {
      return 0;
    }
    switch (s.getType()) {
    case RECORD:
      return compareRecord(o1, o2, s, equals);
    case ENUM:
      return compareEnum(o1, o2, s);
    case ARRAY:
      return compareArray(o1, o2, s, equals);
    case MAP:
      return compareMap(o1, o2, equals);
    case UNION:
      return compareUnion(o1, o2, s, equals);
    case NULL:
      return 0;
    case STRING:
      return compareString(o1, o2);
    default:
      return compareComparable(o1, o2);
    }
  }
  private int compareRecord(Object o1, Object o2, Schema schema, boolean equals) {
    for (Field field : schema.getFields()) {
      if (field.order() == Field.Order.IGNORE) {
        continue;
      }
      int position = field.pos();
      String name = field.name();
      int comparison = compare(getField(o1, name, position),
          getField(o2, name, position), field.schema(), equals);
      if (comparison != 0) {
        return applyFieldOrder(field.order(), comparison);
      }
    }
    return 0;
  }
  private int applyFieldOrder(Field.Order order, int comparison) {
    return order == Field.Order.DESCENDING ? -comparison : comparison;
  }
  private int compareEnum(Object o1, Object o2, Schema schema) {
    return schema.getEnumOrdinal(o1.toString()) - schema.getEnumOrdinal(o2.toString());
  }
  private int compareArray(Object o1, Object o2, Schema schema, boolean equals) {
    Collection<?> first = (Collection<?>) o1;
    Collection<?> second = (Collection<?>) o2;
    Iterator<?> firstIterator = first.iterator();
    Iterator<?> secondIterator = second.iterator();
    Schema elementType = schema.getElementType();
    while (firstIterator.hasNext() && secondIterator.hasNext()) {
      int comparison = compare(firstIterator.next(), secondIterator.next(), elementType, equals);
      if (comparison != 0) {
        return comparison;
      }
    }
    return compareArrayLength(firstIterator.hasNext(), secondIterator.hasNext());
  }
  private int compareArrayLength(boolean leftHasMore, boolean rightHasMore) {
    if (leftHasMore) {
      return 1;
    }
    if (rightHasMore) {
      return -1;
    }
    return 0;
  }
  private int compareMap(Object o1, Object o2, boolean equals) {
    if (equals) {
      return ((Map<?, ?>) o1).equals(o2) ? 0 : 1;
    }
    throw new AvroRuntimeException("Can't compare maps!");
  }
  private int compareUnion(Object o1, Object o2, Schema schema, boolean equals) {
    int firstIndex = resolveUnion(schema, o1);
    int secondIndex = resolveUnion(schema, o2);
    if (firstIndex == secondIndex) {
      return compare(o1, o2, schema.getTypes().get(firstIndex), equals);
    }
    return firstIndex - secondIndex;
  }
  private int compareString(Object o1, Object o2) {
    Utf8 left = o1 instanceof Utf8 ? (Utf8) o1 : new Utf8(o1.toString());
    Utf8 right = o2 instanceof Utf8 ? (Utf8) o2 : new Utf8(o2.toString());
    return left.compareTo(right);
  }
  private int compareComparable(Object o1, Object o2) {
    return ((Comparable<Object>) o1).compareTo(o2);
  }
}
