var runIdFiled = new Ext.form.field.Text({
    name: 'runId',
    fieldLabel: 'Id',
    readOnly: true,
    allowBlank: true
});

var runEditForm = Ext.create('Ext.form.Panel', {
    bodyPadding : 5,
    width : 350,
    url : '/run/save',
    layout : 'anchor',
    defaults : {
        anchor : '100%'
    },
    items : [
		runIdFiled,
        {
            xtype: 'textfield',
            name: 'name',
            fieldLabel: 'Name',
            allowBlank: false
        },
        {
            xtype: 'textarea',
            name: 'description',
            fieldLabel: 'Description',
            allowBlank: false
        },
		Ext.create("Ext.form.ComboBox", {
			editable: false,
			fieldLabel : "Sequencer",
			name : "sequencerId",
			store : sequencersStore,
			displayField : "name",
			valueField : "sequencerId",
		}),
        {
        	xtype: "combobox",
			editable: false,
			fieldLabel : "Project Id",
			name : "projectId",
			store : projectStore,
			displayField : "name",
			valueField : "projectId",
			allowBlank: false
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
        text : 'Submit',
        formBind : true,
        disabled : true,
        handler : function() {
            var form = this.up('form').getForm();
            if (form.isValid()) {
                form.submit({
                    success : function(form, action) {
                    	runs.Store.DataStore.reload();
                    	conveyor.popup.hide();
                        runEditForm.getForm().reset();
                    },
                    failure : function(form, action) {
                    	if (action.result) {
                    		Ext.Msg.alert("System message", action.result.message);
                    	} else {
                    		Ext.Msg.alert("System message", "Cannot save run: remote server is unavailable");
                    	}
                    }
                });
            }
        }
    } ],
});

runsGrid.reconfigure(runs.Store.DataStore);

var runsTab = new Ext.panel.Panel({
	border: false,
	layout: 'border',
	defaults: {
		header: false,
	    collapsible: true,
	    split: true
	},
	items: [ 
	    {
			region: "center",
			id: "runs-center-grid",
			flex : 1
	    },
	    {
			region: "east",
			id: "files-east-grid",
			flex : 1
	    }
	]
});