/*
 * For ExtJS bug fixing:
 * http://www.sencha.com/forum/showthread.php?251896-4.1-Ext.form.action.Submit-onSuccess-causes-exception
 */
Ext.override(Ext.form.action.Submit, {
    handleResponse: function (response) {
        var form = this.form, errorReader = form.errorReader, rs, errors, i, len, records;
        if (errorReader) {
            rs = errorReader.read(response);
            records = rs.records;
            errors = [];
            if (records) {
                for (i = 0, len = records.length; i < len; i++) {
                    errors[i] = records[i].data;
                }
            }
            if (errors.length < 1) {
                errors = null;
            }
            return {
                success: rs.success,
                errors: errors
            };
        }
        if (!response.responseText) {
            if (response.responseXML) {
                try {
                    //FF (+IE?)
                    var sText = response.responseXML.firstChild.children[1].children[0].firstChild.textContent;
                    response.responseText = sText;
                }
                catch (e) {
                    //TODO: Even here we should make sure response.responseText is not undefined - otherwise, fix the onSuccess assumption about handleResponse's return value
                }
            }
        }
        return Ext.decode(response.responseText);
    }
});


/*
 * Read sets namespace.
 */
rs = {}


function addFile() {
	conveyor.popup.show(FileAddForm, "Add read set");
}

function editFile() {

	var selection = filesGrid.getView().getSelectionModel().getSelection()[0];
	if (! selection) {
		return;
	}

	FirstUploadField.setDisabled(true);
	SecondUploadField.setDisabled(true);
	FileTypeField.setDisabled(true);

	var form = FileEditForm.getForm();
	form.findField("isTrimmed").setDisabled(true);
	form.findField("isFixed").setDisabled(true);
//	form.findField("runId").setReadOnly(true);

	if (Ext.Array.contains(["Projects"], Ext.getCmp("conveyor-tab-panel").getActiveTab().title)) {
		conveyor.popup.show(FileEditForm, "Edit read set", runs.Functions.reloadByProjectsGrid);
	} else if (Ext.Array.contains(["Runs"], Ext.getCmp("conveyor-tab-panel").getActiveTab().title)) {
		conveyor.popup.show(FileEditForm, "Edit read set", function() {
			runs.Store.DataStore.loadPage(runs.Store.DataStore.currentPage);
		});
	} else {
		conveyor.popup.show(FileEditForm, "Edit read set", new Function());
	}

	readSetId = selection.get("readSetId");
	Ext.Ajax.request({
		url : "/readSet/load",
		params : {
			readSetId : readSetId
		},
		success : function(response) {
			FormUtil.setValues(form, Ext.JSON.decode(response.responseText).file);
		}
	});
}


/*
 * File types store.
 */
var FileTypesStore = Ext.create('Ext.data.Store', {
    fields: ["type"],
    data : [
        { "type" : "csfasta + qual" },
        { "type" : "fasta + qual" },
        { "type" : "fastq" },
        { "type" : "fasta" },
        { "type" : "csfasta" }
    ]
});


var FileTypeField = Ext.create("Ext.form.ComboBox", {
	editable: false,
	fieldLabel : "Type",
	name : "fileTypeId",
	store : FileTypesStore,
	displayField : "type",
	valueField : "type",
	allowBlank: false,
	listeners : {
		change : function() {
			var value = this.getValue();

			if (value == null) {
				return;
			}

			if (useFileSystemCheckbox.getValue()) {
				var field = SecondUploadExplorer;
			} else {
				var field = SecondUploadField;
			}

			if (/\+/.test(value)) {
				// field.allowBlank = false;
				field.validate();
				field.enable().show();
			} else {
				// field.allowBlank = true;
				field.validate();
				field.disable().hide();
			}
		}
	}
});


/*
 * Sequencers store model.
 */
Ext.define("Sequencers", {
	extend : "Ext.data.Model",
	fields : ["sequencerId", "name"]
});


/*
 * Sequencers store.
 */
var sequencersStore = new Ext.data.JsonStore({
	model : "Sequencers",
	autoLoad : false,
	proxy : {
		type : "ajax",
		url : "/sequencer/list",
		reader : {
			type : "json",
			root : "types"
		}
	}
});


var FirstUploadField = new Ext.form.field.File({
	name : "readSet",
	fieldLabel : "Read file",
	allowBlank : false
});


var SecondUploadField = new Ext.form.field.File({
	name : "qualitySet",
	fieldLabel : "Quality file"
});


var FirstUploadExplorer = new Ext.form.field.Text({
	name : "readSetPath",
	fieldLabel : "Read file",
	hidden : true,
	disabled : true,
	listeners : {
		render : function(c) {
			c.getEl().on('dblclick', function() {
				explorer.Browser.show(FirstUploadExplorer);
			});
		}
	}
});


var SecondUploadExplorer = new Ext.form.field.Text({
	name : "qualitySetPath",
	fieldLabel : "Quality file",
	hidden : true,
	disabled : true,
	listeners : {
		render : function(c) {
			c.getEl().on('dblclick', function() {
				explorer.Browser.show(SecondUploadExplorer);
			});
		}
	}
});


var FileEditForm = Ext.create("Ext.form.Panel", {
	bodyPadding : 5,
	width : 350,
	url : "/readSet/edit",
	layout : "anchor",
	defaults : {
		anchor : "100%"
	},
	defaultType : "textfield",
	items : [
		{
			xtype : "textfield",
			name : "readSetId",
			fieldLabel : "Id",
			readOnly : true,
			allowBlank : true
		},
		{
			xtype : "textfield",
			name : "path",
			fieldLabel : "path",
			disabled : false,
			allowBlank : true,
			listeners : {
				render : function(field) {
					field.getEl().on('dblclick', function() {
						explorer.Browser.show(field);
					});
				}
			}
		},
		{
			xtype : "textareafield",
			name : "description",
			fieldLabel : "Description",
			allowBlank : false
		},
		FileTypeField,
		{
			id : "read-sets-run-combo",
        	xtype: "combobox",
			editable: true,
			fieldLabel : "Run Id",
			name : "runId",
			store : runs.Store.DataStore,
			displayField : "name",
			valueField : "runId",
			allowBlank: false,
			pageSize : 10,
			minChars : 3
		},
		{
			id : "read-sets-sample-combo",
        	xtype: "combobox",
			editable: true,
			fieldLabel : "Sample Id",
			name : "sampleId",
			store : samples.Store.DataStore,
			displayField : "name",
			valueField : "sampleId",
			allowBlank: false,
			pageSize : 10,
			minChars : 3
		},
		{
			xtype : "checkboxfield",
			inputValue: true,
			name : "isTrimmed",
			fieldLabel : "Trimmed?",
			allowBlank : false
		},
		{
			xtype : "checkboxfield",
			inputValue: true,
			name : "isFixed",
			fieldLabel : "Fixed?",
			allowBlank : false
		},
		{
		    xtype: 'textfield',
		    name: 'createDate',
		    fieldLabel: 'Create date',
		    readOnly: true,
		    disabled: true
		}
	],
	buttons : [ {
		text : "Submit",
		formBind : true,
		disabled : true,
		handler : function() {
			var form = this.up("form").getForm();
			if (form.isValid()) {
				form.submit({
					success : function(form, action) {
                    	filesStore.reload();
                    	conveyor.popup.hide(true);
					},
					failure : function(form, action) {
                    	if (action.result) {
                    		Ext.Msg.alert("System message", action.result.message);
                    	} else {
                    		Ext.Msg.alert("System message", "Cannot save read set: remote server is unavailable");
                    	}
					}
				});
			}
		}
	} ],
});


var useFileSystemCheckbox = new Ext.form.field.Checkbox({
	fieldLabel : "Use file system",
	name : "useFileSystem",
	listeners : {
		change : function(checkbox, newValue, oldValue, eOpts) {
			if (newValue) {
				FirstUploadField.hide().disable();
				FirstUploadExplorer.show().enable();
				if (! SecondUploadField.isHidden()) {
					SecondUploadField.hide().disable();
					SecondUploadExplorer.show().enable();
				}
			} else {
				FirstUploadField.show().enable();
				FirstUploadExplorer.hide().disable();
				if (! SecondUploadExplorer.isHidden()) {
					SecondUploadField.show().enable();
					SecondUploadExplorer.hide().enable();
				}
			}
		}
	}
});


var FileAddForm = Ext.create("Ext.form.Panel", {
	bodyPadding : 5,
	width : 350,
	url : "/readSet/save",
	layout : "anchor",
	defaults : {
		anchor : "100%"
	},
	defaultType : "textfield",
	items : [{
			xtype : "textfield",
			name : "readSetId",
			fieldLabel : "Id",
			readOnly : true,
			allowBlank : true
		},
			FileTypeField,
			useFileSystemCheckbox,
			FirstUploadField,
			FirstUploadExplorer,
			SecondUploadField,
			SecondUploadExplorer,
		{
			xtype : "textareafield",
			name : "description",
			fieldLabel : "Description",
			allowBlank : false
		}, {
        	xtype: "combobox",
			editable: true,
			fieldLabel : "Run Id",
			name : "runId",
			store : runs.Store.DataStore,
			displayField : "name",
			valueField : "runId",
			allowBlank: false,
			pageSize : 10,
			minChars : 3
		}, {
        	xtype: "combobox",
			editable: true,
			fieldLabel : "Sample Id",
			name : "sampleId",
			store : samples.Store.DataStore,
			displayField : "name",
			valueField : "sampleId",
			allowBlank: false,
			pageSize : 10,
			minChars : 3
		}, {
			id : "close-window-checkbox",
			xtype : "checkbox",
			fieldLabel : "Do not close window",
			listeners : {
				change : function(checkbox, newValue, oldValue, eOpts) {
					
				}
			}
			
		}
	],
	buttons : [ {
		text : "Submit",
		formBind : true,
		disabled : true,
		handler : function() {
			var form = this.up("form").getForm();
			if (form.isValid()) {
				form.submit({
					success : function(form, action) {
                    	filesStore.reload();
                    	if (! Ext.getCmp("close-window-checkbox").getValue()) {
                    		conveyor.popup.hide(true);
                    	}
					},
					failure : function(form, action) {
                    	if (action.result) {
                    		Ext.Msg.alert("System message", action.result.message);
                    	} else {
                    		Ext.Msg.alert("System message", "Cannot save read set: remote server is unavailable");
                    	}
					}
				});
			}
		}
	} ],
});


var filesStore = Ext.create("Ext.data.Store", {
	storeId : "filesStore",
	fields : [ "readSetId", "seqsFile", "seqsSample" ],
	remoteSort : true,
	pageSize : 45,
	proxy : {
		type : "ajax",
		url : "/readSet/list",
		reader : {
			type : "json",
			root : "files",
			totalProperty : "totalCount"
		}
	},
	listeners : {
		beforeload: function(store, oper) {
			if (! ("params" in oper)) {
				oper.params = {}
			}

			var activeTab = conveyor.Panel.getActiveTab();
			if (activeTab) {
				var activeTabIndex = conveyor.Panel.items.findIndex('id', activeTab.id);
				if (activeTabIndex == 0 || activeTabIndex == 1) {
					selection = runsGrid.getView().getSelectionModel().getSelection()[0];
					if (selection) {
						oper.params.runId = runsGrid.getView().getSelectionModel().getSelection()[0].get("runId")
					}
				} 
			}

			var readSetName = Ext.String.trim(rs.FilepathFilter.getValue())
			if (readSetName.length == 0 || readSetName.length > 2) {
				oper.params.name = readSetName
			}
		}
	}
});


rs.FilepathFilter = new Ext.form.field.Text({
	fieldLabel : "Filter",
	listeners : {
		change : function(textfield, value) {

			value = Ext.String.trim(value)
			if (value.length == 0 || value.length > 2) {
				filesStore.load()
			}
		}
	}
});


var filesGrid = Ext.create("Ext.grid.Panel", {
	border: false,
	header : false,
	loadMask : true,
	selModel : Ext.create("Ext.selection.CheckboxModel"),
	store : Ext.data.StoreManager.lookup("filesStore"),
	columns : [ {
		xtype: 'rownumberer'
	}, {
		text : "id",
		dataIndex : "readSetId"
	}, {
		text : "name",
		flex : true,
		dataIndex : "seqsFile",
		renderer: function(seqsFile, meta, record) {
			return seqsFile.name;
		}
	}, {
		text : "type",
		dataIndex : "seqsFile",
		sortable : false,
		renderer: function(seqsFile, meta, record) {
			if (seqsFile.seqsFileType) {
				return seqsFile.seqsFileType.type;
			} else {
				return "";
			}
		}
	}, {
		text : "sample",
		dataIndex : "seqsSample",
		renderer: function(seqsSample, meta, record) {
			return seqsSample.name;
		},
		getSortParam : function() {
			return "seqsSample.name";
		}
	}, {
		text : "description",
		dataIndex : "seqsFile",
		sortable : false,
		flex : true,
		renderer: function(seqsFile, meta, record) {
			return seqsFile.description;
		}
	}, /*{
		xtype: "checkcolumn",
		text : "fixed",
		dataIndex : "seqsFile",
		width: 35,
		sortable : false,
		renderer: function(seqsFile, meta, record) {
			return (new Ext.ux.CheckColumn()).renderer(seqsFile.seqsRunsFile.isFixed);
		}
	}, {
		xtype: "checkcolumn",
		text : "trimmed",
		dataIndex : "seqsFile",
		width: 52,
		sortable : false,
		renderer: function(seqsFile, meta, record) {
			return (new Ext.ux.CheckColumn()).renderer(seqsFile.seqsRunsFile.isTrimmed);
		}
	},*/ {
		text : "size, bytes",
		dataIndex : "seqsFile",
		renderer: function(seqsFile, meta, record) {
			return seqsFile.volume;
		},
		getSortParam : function() {
			return "seqsFile.volume";
		}
	}, {
		text : "create date",
		width : 120,
		dataIndex : "seqsFile",
		renderer: function(seqsFile, meta, record) {
			return seqsFile.createDate;
		},
		getSortParam : function() {
			return "seqsFile.createDate";
		}
	} ],
	bbar : Ext.create("Ext.PagingToolbar", {
		store : filesStore,
		displayInfo : true,
		displayMsg : "Displaying files {0} - {1} of {2}",
		emptyMsg : "No files to display"
	}),
	dockedItems : [{
		xtype : "toolbar",
		items : [
			{
				html: "Read sets:",
				xtype: "label"
			}, {
				text : "Add",
				iconCls : "icon-add",
				handler : addFile
			}, "-", {
				text : "Edit",
				iconCls : "icon-edit",
				handler : editFile
			}, "-", {
				text : "Delete",
				iconCls : "icon-delete",
				handler : function() {

					Ext.Msg.defaultButton = "no";
					Ext.Msg.show({
						msg: "remove records with files?",
						buttons: Ext.Msg.YESNOCANCEL,
						fn: function(btn) {
							switch (btn) {
								case "yes" : url = "/readSet/delete?withFile=true"; break;
								case "no"  : url = "/readSet/delete?withFile=false"; break;
								default    : url = ""; break;
							}

							if (url == "") {
								return;
							}

							var selection = filesGrid.getView().getSelectionModel().getSelection()[0];
							if (! selection) {
								return;
							}

							Ext.Ajax.request({
								url : url,
								params : {
									readSetId : selection.get("readSetId")
								},
								success : function(response) {
									var json = Ext.JSON.decode(response.responseText);
									if (json.success) {
										filesStore.remove(selection);
									} else {
										Ext.Msg.alert("System message", json.message);
									}
								},
								failure : function() {
									Ext.Msg.alert("System message", "Cannot delete read set: remote server is unavailable");
								}
							});
						}
					})
				}
			},
			"-",
			rs.FilepathFilter
		]
	}],
	listeners : {
		beforeitemdblclick : function(grid, record, item, index, event) {
			editFile();
		}
	}
});