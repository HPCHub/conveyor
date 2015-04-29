var FileUtil = {};
FileUtil.getDirectory = function(path) {

	return path.substring(0, path.lastIndexOf("/"));
}


var StoreUtil = {};
StoreUtil.findValueBy = function(key, value, store, q) {

	var index = store.findExact(key, value);
	if (index < 0) {
		return null;
	}

	var record = store.getAt(index);
	if (! record) {
		return null;
	}

	return record.get(q);
}


var ModelUtil = {

	toArray : function(model, field) {

		var result = new Array(model.length);
		for (i in model) {
			result[i] = model[i].get(field)
		}

		return result;
	}
}


var FormUtil = {

	setValues : function(form, values, list) {

		var list = list || {};

        for (i in values) {
        	if (values[i] instanceof Object) {
        		FormUtil.setValues(form, values[i], list);
        	}

        	var field = form.findField(i);
        	if (field == null) {
        		continue;
        	}

        	if (field && values[i] && ! field.getValue() && ! (i in list)) {
        		if (field.getXType() == "combobox") {
        			var store = field.getStore();
        			var param = store.getProxy().getModel().getFields()[0].name;
        			var params = {};
        			params[param] = values[i];
        			store.load({
            			params : params
            		});
        		}
        		field.setValue(values[i]);
        		list.i = true;
        	}
        }
	}
}