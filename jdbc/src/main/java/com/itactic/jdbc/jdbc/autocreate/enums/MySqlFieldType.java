/**package com.itactic.jdbc.jdbc.autocreate.enums;


public enum MySqlFieldType {

     Integer(java.lang.Integer.class,"int"),
     Float(java.lang.Float.class,"float"),
     Decimal(java.math.BigDecimal.class,"decimal"),
     Double(java.lang.Double.class,"double"),
     Date(java.util.Date.class,"datetime"),
     String(java.lang.String.class,"varchar");

     private Class cls;
     private String str;

     MySqlFieldType(Class cls, String str) {
          this.cls = cls;
          this.str = str;
     }

     public static String getDataBaseTypeByObject(Class<?> obj){
          for (MySqlFieldType fieldType: MySqlFieldType.values()){
               if (fieldType.cls == obj.getClass()) {
                    return fieldType.str;
               }
          }
          return "varchar";
     }
}
*/