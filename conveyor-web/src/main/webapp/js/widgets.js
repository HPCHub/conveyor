var runs = {};


runs.Store = {};


/*
 * Runs store.
 */
runs.Store.CurrentRecord = null;
runs.Store.DataStore = Ext.create('Ext.data.Store', {
	model : "Runs",
	fields : ["runId", 'name', 'description', "sequencerId", "seqsProject", "sampleId", "createDate"],
	remoteSort : true,
	autoLoad : false,
	proxy: {
		type : 'ajax',
	    url: '/run/list',
	    reader: {
	    	type : 'json',
	        root: 'runs',
	        totalProperty: 'totalCount'
	    }
	},
	listeners : {
		beforeload : function(store, operation, eOpts) {
			var value = Ext.getCmp("read-sets-run-combo").getValue();
			var record = store.findRecord("runId", value, 0, false, true, true);
			if (record) {
				runs.Store.CurrentRecord = record;
			}
		},
		load : function(store) {
			if (runs.Store.CurrentRecord) {
				store.add(runs.Store.CurrentRecord);
			}
		}
	}
});


runs.Functions = {}


runs.Functions.reloadByProjectsGrid = function() {
    var selection = projectsGrid.getView().getSelectionModel().getSelection()[0];
    if (selection) {
        runs.Store.DataStore.load({
            params: {projectId: selection.get("projectId")}
        });
    }
}


function addRun() {

	conveyor.popup.show(runEditForm, "Add run");
	runIdFiled.setDisabled(true);
}

function editRun() {
	
    var selection = runsGrid.getView().getSelectionModel().getSelection()[0];
    if (! selection) {
    	return;
    }

    conveyor.popup.show(runEditForm, "Edit run");
    runIdFiled.setDisabled(false);
	runId = selection.get("runId");
	Ext.Ajax.request({
	    url: '/run/load',
	    params: {
	    	runId: runId
	    },
	    success: function(response){
	        FormUtil.setValues(runEditForm.getForm(), Ext.JSON.decode(response.responseText).run);
	    }
	});
}


var runsGrid = Ext.create('Ext.grid.Panel', {
	border: false,
    header: false,
    loadMask: true,
    selModel : Ext.create('Ext.selection.CheckboxModel'),
    columns: [
        {
        	text: 'id',
        	dataIndex: 'runId'
        },
        {
        	text: 'name',
        	dataIndex: 'name'
        },
        {
        	text: 'description',
        	dataIndex: 'description',
        	flex: true
        },
        {
    		text : "sequencer",
    		dataIndex : "sequencerId",
    		renderer: function(sequencerId, meta, record) {
    			return StoreUtil.findValueBy("sequencerId", sequencerId, sequencersStore, "name");
    		}
    	},
        {
        	text: 'project',
        	dataIndex: 'seqsProject',
        	renderer: function(seqsProject, meta, record) {
        		return StoreUtil.findValueBy("projectId", seqsProject.projectId, projectStore, "name");
        	}
        },
        {
        	text: 'date',
        	dataIndex: 'createDate'
        }
    ],
    bbar: Ext.create('Ext.PagingToolbar', {
        store : runs.Store.DataStore,
        displayInfo: true,
        displayMsg: 'Displaying runs {0} - {1} of {2}',
        emptyMsg: "No runs to display"
    }),
    listeners : {
    	select: function(grid, record, item, index) {
    		filesStore.loadPage(1, {params: {
				runId : record.get("runId")
			}});
    	}
    },
    dockedItems: [{
        xtype: 'toolbar',
        items: [
            {
            	html: "Runs: ",
            	xtype: "label"
            }, {
	            text: 'Add',
	            iconCls: 'icon-add',
	            handler: addRun
	        }, '-', {
	            text: 'Edit',
	            iconCls: 'icon-edit',
	            handler: editRun
	        }, '-', {
	            text: 'Delete',
	            iconCls: 'icon-delete',
	            handler: function() {
	                var selection = runsGrid.getView().getSelectionModel().getSelection()[0];
	                if (! selection) {
	                	return;
	                }
	
	            	Ext.Ajax.request({
	            	    url: '/run/delete',
	            	    params: {
	            	    	runId: selection.get("runId")
	            	    },
						success : function(response) {
							var json = Ext.JSON.decode(response.responseText);
							if (json.success) {
								runs.Store.DataStore.remove(selection);
							} else {
								Ext.Msg.alert("System message", json.message);
							}
						},
						failure : function() {
							Ext.Msg.alert("System message", "Cannot delete run: remote server is unavailable");
						}
	            	});
	            }
	        }
	    ]
    }]
});