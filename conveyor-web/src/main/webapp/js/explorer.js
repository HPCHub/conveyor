var explorer = {};


/*
 * Files store.
 */
explorer.FilesStore = new Ext.data.JsonStore({
	fields : [ "absolute", "absoluteFile", "absolutePath", "canonicalPath", "directory" , "file", "freeSpace", "hidden", "name", "parent", "path", "totalSpace", "usableSpace" ],
	proxy : {
		type : "ajax",
		url : "/file/list",
		reader : {
			type : "json",
			root : "files",
			totalProperty : "totalCount"
		}
	},
	sorters: [{
        sorterFn: function(o1, o2){
            if (o1.data.name == "..") {
                return -1;
            } else {
            	return 0;
            }
        }
    }],
	listeners : {
		load : function(store, records, successful, eOpts) {
			if (explorer.AddressString.getValue() != "/") {
				store.add({
					absolute : "",
					absoluteFile : "",
					absolutePath : "",
					canonicalPath : "",
					directory : true,
					file : false,
					freeSpace : "",
					hidden : "",
					name : "..",
					parent : "",
					path : "",
					totalSpace : "",
				usableSpace : ""});
			}
		}
	}
});


explorer.AddressString = new Ext.form.field.Text({
	value : "/",
	readOnly : true,
	flex: 1,
});


explorer.FilesGridPager = new Ext.PagingToolbar({
	store : explorer.FilesStore,
	displayInfo : true,
	displayMsg : "Displaying files {0} - {1} of {2}",
	emptyMsg : "No files to display"
})


explorer.FilesGrid = Ext.create("Ext.grid.Panel", {
	header : false,
	loadMask : true,
	store : explorer.FilesStore,
	columns : [
	new Ext.grid.RowNumberer(),
	{
		flex : true,
		text : "name",
		dataIndex : "name",
		renderer: function(name, meta, record) {
			if (record.get("directory") && name != "..") {
				name = name + "/";
			}

			return name;
		}
	}, {
		text : "size",
		dataIndex : "absolutePath",
		renderer: function(absolutePath, meta, record) {
			if (record.get("directory")) {
				return "&lt;DIR&gt;";
			} else {
				return "";
			}
		}
	} ],
	listeners : {
		beforeitemdblclick : function(grid, record) {
			if (record.get("file")) {
				return explorer.Browser.close(record.get("absolutePath"));
			}

			if (record.get("name") == "..") {
				var directory = explorer.AddressString.getValue();
				var index = directory.lastIndexOf("/");
				console.log(index);
				if (index == 0) {
					directory = "/";
				} else {
					directory = directory.substring(0, index);
				}
			} else {
				var directory = record.get("absolutePath");
			}

			explorer.AddressString.setValue(directory);
			explorer.FilesStore.load({params : {
				directory : directory
			}});
		}
	},
	bbar : explorer.FilesGridPager,
	dockedItems : [ {
		xtype : "toolbar",
		items : [ explorer.AddressString ]
	} ]
});


explorer.Browser = new function() {
	this.field;
	this.window = null;
	this.getWindow = function() {
		if (this.window == null) {
			this.window = new Ext.Window({
				layout : "fit",
				width : Ext.getBody().getWidth() * 0.8,
				height : Ext.getBody().getHeight() * 0.8,
				closeAction : "hide",
				plain : true,
				items : [ explorer.FilesGrid ],
				listeners : {
					close : function() {

					}
				}
			});
		}

		return this.window;
	}

	this.show = function(field) {
		this.field = field;

		if (field.getValue()) {
			directory = FileUtil.getDirectory(field.getValue());
		} else {
			directory = explorer.AddressString.getValue();
		}

		explorer.AddressString.setValue(directory);

		explorer.FilesStore.load({params : {
			directory : directory
		}});

		this.getWindow().show();
	};

	this.close = function(value) {
		this.getWindow().hide();
		this.field.setValue(value);
	}
};