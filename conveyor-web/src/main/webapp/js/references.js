function addReference() {

	conveyor.popup.show(referenceEditForm, "Add reference");
	referenceEditForm.getForm().findField("referenceId").setDisabled(true);
	referenceUploadField.setDisabled(false);
}

function editReference() {

	var selection = ReferencesGrid.getView().getSelectionModel().getSelection()[0];
	if (!selection) {
		return;
	}

	conveyor.popup.show(referenceEditForm, "Edit reference");
	referenceUploadField.setDisabled(true);

	var f = referenceEditForm.getForm();
	var v = f.findField("referenceId");
	v.setDisabled(false);

	referenceId = selection.get("referenceId");
	Ext.Ajax.request({
		url : "/reference/load",
		params : {
			referenceId : referenceId
		},
		success : function(response) {
			FormUtil.setValues(referenceEditForm.getForm(), Ext.JSON.decode(response.responseText).reference);
		}
	});
}


var referenceUploadField = new Ext.form.field.File({
	name : "upload",
	fieldLabel : "Reference",
	allowBlank : false
});


var typesStore = Ext.create('Ext.data.Store', {
    fields: ["type"],
    data : [
        {"type":"nucleotide"},
        {"type":"protein"},
    ]
});


var typeField = Ext.create("Ext.form.ComboBox", {
	editable: false,
	fieldLabel : "Type",
	name : "type",
	store : typesStore,
	displayField : "type",
	valueField : "type",
	allowBlank: false
});


var referenceEditForm = Ext.create("Ext.form.Panel", {
	bodyPadding : 5,
	width : 350,
	url : "/reference/save",
	layout : "anchor",
	defaults : {
		anchor : "100%"
	},
	defaultType : "textfield",
	items : [
		{
			xtype : "textfield",
			name : "referenceId",
			fieldLabel : "Id",
			readOnly : true,
			allowBlank : true
		},
		referenceUploadField,
		typeField,
		{
			xtype : "textfield",
			name : "organism",
			fieldLabel : "Organism",
			allowBlank : false
		},
		{
			xtype : "textareafield",
			name : "description",
			fieldLabel : "Description",
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
                    	ReferencesStore.reload();
                    	conveyor.popup.hide();
                    	referenceEditForm.getForm().reset();
					},
					failure : function(form, action) {
						Ext.Msg.alert("Failed", action.result.msg);
					}
				});
			}
		}
	} ],
});


var ReferencesStore = Ext.create("Ext.data.Store", {
	storeId : "ReferencesStore",
	fields : [ "referenceId", "organism", "type", "seqsFile"],
	proxy : {
		type : "ajax",
		url : "/reference/list",
		reader : {
			type : "json",
			root : "references",
			totalProperty : "totalCount"
		}
	}
});

var ReferencesGrid = Ext.create("Ext.grid.Panel", {
	border: false,
	header : false,
	loadMask : true,
	selModel : Ext.create("Ext.selection.CheckboxModel"),
	store : Ext.data.StoreManager.lookup("ReferencesStore"),
	columns : [ {
		text : "id",
		dataIndex : "referenceId"
	}, {
		text : "organism",
		dataIndex : "organism"
	}, {
		text : "type",
		dataIndex : "type"
	}, {
		text : "description",
		dataIndex : "seqsFile",
		flex : true,
		renderer: function(seqsFile, meta, record) {
			return seqsFile.description;
		}
	}, {
		text : "file",
		dataIndex : "seqsFile",
		flex : true,
		renderer: function(seqsFile, meta, record) {
			return seqsFile.path;
		}
	}, {
		text : "size",
		dataIndex : "seqsFile",
		renderer: function(seqsFile, meta, record) {
			return seqsFile.volume;
		}
	}, {
		text : "create date",
		dataIndex : "seqsFile",
		renderer: function(seqsFile, meta, record) {
			return seqsFile.createDate;
		}
	} ],
	bbar : Ext.create("Ext.PagingToolbar", {
		store : ReferencesStore,
		displayInfo : true,
		displayMsg : "Displaying references {0} - {1} of {2}",
		emptyMsg : "No references to display"
	}),
	dockedItems : [{
		xtype : "toolbar",
		items : [
			{
				html: "References:",
				xtype: "label"
			}, {
				text : "Add",
				iconCls : "icon-add",
				handler : addReference
			}, "-", {
				text : "Edit",
				iconCls : "icon-edit",
				handler : editReference
			}, "-", {
				text : "Delete",
				iconCls : "icon-delete",
				handler : function() {
					var selection = ReferencesGrid.getView().getSelectionModel()
							.getSelection()[0];
					if (!selection) {
						return;
					}

					Ext.Ajax.request({
						url : "/reference/delete",
						params : {
							referenceId : selection.get("referenceId")
						},
						success : function(response) {
							ReferencesStore.remove(selection);
						}
					});
				}
			}
		]
	}]
});