package com.nabsys.nabeeplus.listener;

import com.nabsys.nabeeplus.views.model.TableModel;

public interface NBTableModifiedListener {
	public void modified(TableModel model, String id, String fieldName, int fieldIndex);
}
