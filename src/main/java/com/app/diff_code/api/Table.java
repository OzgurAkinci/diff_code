package com.app.diff_code.api;

import java.util.Hashtable;

class Table {
  Hashtable table = new Hashtable();
  public void put(String id, Ratio val) {
  	table.put(id, val);
  }
  public Ratio get(String id) {
  	return (Ratio)table.get(id);
  }
}