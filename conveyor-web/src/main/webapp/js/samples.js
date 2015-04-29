samples = {}
samples.checkbox = {}

function addSample() {

	conveyor.popup.show(sampleEditForm, "Add sample");
    sampleEditForm.getForm().findField("sampleId").setDisabled(true);
}

function editSample() {

    var selection = samplesGrid.getView().getSelectionModel().getSelection()[0];
    if (! selection) {
    	return;
    }

    conveyor.popup.show(sampleEditForm, "Edit sample");
	sampleEditForm.getForm().findField("sampleId").setDisabled(false);
	sampleId = selection.get("sampleId");
	Ext.Ajax.request({
	    url: '/sample/load',
	    params: {
	    	sampleId: sampleId
	    },
	    success: function(response){
	        var sample = Ext.JSON.decode(response.responseText).sample;
	        var form = sampleEditForm.getForm();
	        for (i in sample) {
	        	var field = form.findField(i);
	        	if (field && sample[i]) {
	        		field.setValue(sample[i]);
	        	}
	        }
	    }
	});
}


samples.checkbox.doNotCloseWindow = new Ext.form.field.Checkbox({
	fieldLabel : "Do not close window",
})


var sampleEditForm = Ext.create('Ext.form.Panel', {
    bodyPadding : 5,
    width : 350,
    url : '/sample/save',
    layout : 'anchor',
    defaults : {
        anchor : '100%'
    },
    items : [
		{
		    xtype: 'textfield',
		    name: 'sampleId',
		    fieldLabel: 'Id',
		    readOnly: true,
		    allowBlank: true
		},
        {
            xtype: 'textfield',
            name: 'name',
            fieldLabel: 'Tag',
            allowBlank: false
        },
        {
            xtype: 'textarea',
            name: 'description',
            fieldLabel: 'Description'
        },
        {
		    xtype: 'textfield',
		    name: 'createDate',
		    fieldLabel: 'Create date',
		    readOnly: true,
		    disabled: true
		}, samples.checkbox.doNotCloseWindow
    ],
    buttons : [ {
        text : 'Submit',
        formBind : true,
        disabled : true,
        handler : function() {
            var form = this.up('form').getForm();
            if (form.isValid()) {
                form.submit({
                    success : function(form, action) {
                    	samples.Store.DataStore.reload();
                    	console.log(samples.checkbox.doNotCloseWindow.getValue())
                    	if (! samples.checkbox.doNotCloseWindow.getValue()) {
                    		conveyor.popup.hide();
                    		sampleEditForm.getForm().reset();
                    	}
                    },
                    failure : function(form, action) {
                    	if (action.result) {
                    		Ext.Msg.alert("System message", action.result.message);
                    	} else {
                    		Ext.Msg.alert("System message", "Cannot save sample: remote server is unavailable");
                    	}
                    }
                });
            }
        }
    } ],
});


Ext.define("Samples", {
	extend : "Ext.data.Model",
	fields:["sampleId", 'name', 'description', 'createDate']
});


samples.Store = {};
samples.Store.CurrentRecord = null;
samples.Store.DataStore = Ext.create('Ext.data.Store', {
	model: "Samples",
    autoLoad : false,
    pageSize : 45,
	proxy: {
		type : 'ajax',
	    url: '/sample/list',
	    reader: {
	    	type : 'json',
	        root: 'samples',
	        totalProperty: 'totalCount'
	    }
	},
	listeners : {
		beforeload : function(store, operation, eOpts) {
			var value = Ext.getCmp("read-sets-sample-combo").getValue();
			var record = store.findRecord("sampleId", value, 0, false, true, true);
			if (record) {
				samples.Store.CurrentRecord = record;
			}
		},
		load : function(store) {
			if (samples.Store.CurrentRecord) {
				store.add(samples.Store.CurrentRecord);
			}
		}
	}
});


var samplesGrid = Ext.create('Ext.grid.Panel', {
    header: false,
    loadMask: true,
    selModel : Ext.create('Ext.selection.CheckboxModel'),
    store: samples.Store.DataStore,
    columns: [
        { text: 'id',  dataIndex: 'sampleId' },
        { text: 'name', dataIndex: 'name'},
        { text: 'description', dataIndex: 'description', flex: true},
        { text: 'create date', dataIndex: 'createDate' }
    ],
   	bbar: Ext.create('Ext.PagingToolbar', {
        store: samples.Store.DataStore,
        displayInfo: true,
        displayMsg: 'Displaying samples {0} - {1} of {2}',
        emptyMsg: "No samples to display"
    }),
    dockedItems: [{
        xtype: 'toolbar',
        items: [
            {
            	html: "Samples: ",
            	xtype: "label"
            }, {
	            text: 'Add',
	            iconCls: 'icon-add',
	            handler: addSample
	        }, '-', {
	            text: 'Edit',
	            iconCls: 'icon-edit',
	            handler: editSample
	        }, '-', {
	            text: 'Delete',
	            iconCls: 'icon-delete',
	            handler: function() {
	                var selection = samplesGrid.getView().getSelectionModel().getSelection()[0];
	                if (! selection) {
	                	return;
	                }
	
	            	Ext.Ajax.request({
	            	    url: '/sample/delete',
	            	    params: {
	            	    	sampleId: selection.get("sampleId")
	            	    },
						success : function(response) {
							var json = Ext.JSON.decode(response.responseText);
							if (json.success) {
								samples.Store.DataStore.remove(selection);
							} else {
								Ext.Msg.alert("System message", json.message);
							}
						},
						failure : function() {
							Ext.Msg.alert("System message", "Cannot delete sample: remote server is unavailable");
						}
	            	});
	            }
	        }
	    ]
    }]
});