function addProject() {
	conveyor.popup.show(ProjectEditForm, "Add project");
    ProjectEditForm.getForm().findField("projectId").setDisabled(true);
}

function editProject() {
	
    var selection = projectsGrid.getView().getSelectionModel().getSelection()[0];
    if (! selection) {
    	return;
    }

    conveyor.popup.show(ProjectEditForm, "Edit project");
	ProjectEditForm.getForm().findField("projectId").setDisabled(false);
	projectId = selection.get("projectId");
	Ext.Ajax.request({
	    url: '/project/load',
	    params: {
	    	projectId: projectId
	    },
	    success: function(response){
	        var project = Ext.JSON.decode(response.responseText).project;
	        var form = ProjectEditForm.getForm();
	        for (i in project) {
	        	var field = form.findField(i);
	        	if (field && project[i]) {
	        		field.setValue(project[i]);
	        	}
	        }
	    }
	});
}


var ProjectEditForm = Ext.create('Ext.form.Panel', {
    bodyPadding : 5,
    width : 350,
    url : '/project/save',
    layout : 'anchor',
    defaults : {
        anchor : '100%'
    },
    items : [
		{
		    xtype: 'textfield',
		    name: 'projectId',
		    fieldLabel: 'Id',
		    readOnly: true,
		    allowBlank: true
		},
        {
            xtype: 'textfield',
            name: 'name',
            fieldLabel: 'Name',
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
                    	projectStore.reload();
                    	conveyor.popup.hide();
                        ProjectEditForm.getForm().reset();
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


/*
 * Projects store model.
 */
Ext.define("Projects", {
	extend : "Ext.data.Model",
	fields:["projectId", {
		name : "name",
		sortType : "asUCText",
	}, 'description', 'createDate', "rootDir"]
});


/*
 * Projects store.
 */
var projectStore = Ext.create('Ext.data.Store', {
	model : "Projects",
	pageSize : 45,
	autoLoad : false,
	proxy: {
		type : 'ajax',
	    url: '/project/list',
	    reader: {
	    	type : 'json',
	        root: 'projects',
	        totalProperty: 'totalCount'
	    }
	}
});


var projectsGrid = Ext.create('Ext.grid.Panel', {
	border: false,
    header: false,
    loadMask: true,
    selModel : Ext.create('Ext.selection.CheckboxModel'),
    store: projectStore,
    columns: [
        { text: 'id',  dataIndex: 'projectId' },
        { text: 'name', dataIndex: 'name'},
        { text: 'description', dataIndex: 'description', flex: true},
        { text: 'create date', dataIndex: 'createDate' }
        //,{ text: 'root direcotry', dataIndex: 'rootDir' }
    ],
   	bbar: Ext.create('Ext.PagingToolbar', {
        store: projectStore,
        displayInfo: true,
        displayMsg: 'Displaying projects {0} - {1} of {2}',
        emptyMsg: "No projects to display"
    }),
    listeners : {
    	select: function(grid, record, item, index) {
    		runs.Store.DataStore.load({
    			params: {projectId: record.get("projectId")}
    		});
    	}
    },
    dockedItems: [{
        xtype: 'toolbar',
        items: [
            {
            	html: "Projects: ",
            	xtype: "label"
            }, {
	            text: 'Add',
	            iconCls: 'icon-add',
	            handler: addProject
	        }, '-', {
	            text: 'Edit',
	            iconCls: 'icon-edit',
	            handler: editProject
	        }, '-', {
	            text: 'Delete',
	            iconCls: 'icon-delete',
	            handler: function() {
	                var selection = projectsGrid.getView().getSelectionModel().getSelection()[0];
	                if (! selection) {
	                	return;
	                }
	
	            	Ext.Ajax.request({
	            	    url: '/project/delete',
	            	    params: {
	            	    	projectId: selection.get("projectId")
	            	    },
						success : function(response) {
							var json = Ext.JSON.decode(response.responseText);
							if (json.success) {
								projectStore.remove(selection);
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


var projectsTab = new Ext.panel.Panel({
	border: false,
	layout: {
		type: 'border'
	},
	defaults: {
		header: false,
	    collapsible: true,
	    split: true,
	    border: true
	},
	items: [ 
	    {
			region: "west",
			id: "projects-tab-projects-grid",
			flex : 1
	    },
	    {
			region: "center",
			id: "projects-tab-runs-grid",
			flex : 1
	    },
	    {
			region: "east",
			id: "projects-tab-files-grid",
			flex : 1
	    }
	]
});