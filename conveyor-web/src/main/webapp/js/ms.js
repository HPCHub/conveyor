var ms = {};

ms.FilesStore = Ext.create('Ext.data.Store', {
	fields:["fileId", "fileName"],
    autoLoad : false,
	proxy: {
		type : 'ajax',
	    url: '/ms/files',
	    reader: {
	    	type : 'json',
	        root: 'files',
	        totalProperty: 'totalCount'
	    }
	}
});


ms.FilesGrid = Ext.create('Ext.grid.Panel', {
    header: false,
    width: 250,
    loadMask: true,
    store: ms.FilesStore,
    columns: [
        {
        	text: 'file',
        	dataIndex: 'fileName',
        	flex: true,
        	renderer : function(fileName) {
        		return fileName.split("/").slice(-1)[0];
        	}
		}
    ],
    listeners : {
    	select : function(grid, record, index) {
    		ms.DataStore.load({
                params: {
                	fileId : record.get("fileId")
                }
            });
    	}
    }
});


ms.ImageWindow = new function() {

	this.window = new Ext.Window({
	    layout : "fit",
	    width : 1200,
	    height : 540,
	    closeAction : "hide",
	    plain : true,
	    modal : true,
	    items : [ {
	    	xtype : "panel",
	    	layout : "hbox",
	    	defaults : {
	    		border : false,
	    		width : 600,
	    		height : 480,
	    		margin : 10
	    	},
	    	items : [
	    	    {
	    	    	id: "ms-image-spectre"
	    	    },
	    	    {
	    	    	id: "ms-image-molecule"
	    	    }
	        ]
	    } ]
	});
	
	this.show = function(innerId, issueId) {

		Ext.getCmp("ms-image-spectre").update(
				'<img src="/ms/spectre?issueId=' + issueId + '" />'
		);

		Ext.getCmp("ms-image-molecule").update(
			'<img style="padding-top: 70px" src="http://metlin.scripps.edu/Mol_images/' + innerId + '.png" />'
		);

		this.window.show();
	};
};


ms.DataStore = Ext.create('Ext.data.Store', {
	fields:["isCredible", "innerId", "issueId", "msIon", "name", "precursor", "precursorPpm", "score"],
    autoLoad : false,
    remoteSort : true,
	proxy: {
		type : 'ajax',
	    url: '/ms/list',
	    reader: {
	    	type : 'json',
	        root: 'msIons',
	        totalProperty: 'totalCount'
	    }
	},
	listeners : {
		beforeload: function(store, oper) {
			if (ms.FilesGrid.getView().getSelectionModel().getSelection()[0]) {
				oper.params = {
					fileId: ms.FilesGrid.getView().getSelectionModel().getSelection()[0].get("fileId")
				}
			}
		}
	}
});


ms.PeaksStore = Ext.create('Ext.data.Store', {
	fields:["isCredible", "innerId", "issueId", "msIon", "name", "precursor", "precursorPpm", "score"],
    autoLoad : false,
    remoteSort : true,
	proxy: {
		type : 'ajax',
	    url: '/ms/peaks',
	    reader: {
	    	type : 'json',
	        root: 'msIons',
	        totalProperty: 'totalCount'
	    }
	}
});


ms.PagingToolbar = Ext.create('Ext.PagingToolbar', {
    store: ms.DataStore,
    displayInfo: true,
    displayMsg: 'Displaying ms {0} - {1} of {2}',
    emptyMsg: "No ms to display"
});


ms.DataGrid = Ext.create('Ext.grid.Panel', {
	flex : true,
    header: false,
    loadMask: true,
    store: ms.DataStore,
    columns: [
		{
			xtype: "checkcolumn",
			dataIndex : "isCredible",
			width: 35,
			listeners : {
				checkchange : function(column, rowIndex, checked) {
					if (checked) {
						var url = "/ms/validate";
					} else {
						var url = "/ms/invalidate";
					}

	            	Ext.Ajax.request({
	            	    url: url,
	            	    params: {
	            	    	issueId: ms.DataGrid.getStore().getAt(rowIndex).get("issueId")
	            	    }
	            	});
				}
			}
		},
        {
        	text: 'name',
        	dataIndex: 'name',
        	flex : true
		},
        {
			text: 'score', 
			dataIndex: 'score',
			align : "center"
		},
        {
			text: 'retentionTime',
			dataIndex: 'msIon',
			align : "center",
			renderer : function(msIon) {
				return msIon.retentionTime;
			},
			getSortParam : function() {
				return "msIon.retentionTime";
			}
		},
        {
			text: 'm/z',
			dataIndex: 'msIon',
			align : "center",
			flex : true,
			renderer : function(msIon) {
				return msIon.mass;
			},
			getSortParam : function() {
				return "msIon.mass";
			}
		},
        {
			text: 'precursor',
			dataIndex: 'precursor',
			align : "center"
		},
        {
			text: 'precursorPpm',
			dataIndex: 'precursorPpm',
			align : "center"
		},
        {
			text: 'амлекула',
			dataIndex: 'innerId',
			sortable: false,
			width: 220,
			renderer : function(innerId) {
				return '<img src="http://metlin.scripps.edu/Mol_images/' + innerId + '.png" width="200" />';
			}
		},
		{
			text: 'spectrumMatching',
			dataIndex: "issueId",
			sortable: false,
			width: 220,
			renderer : function(issueId) {
				 return '<img src="/ms/spectre?issueId=' + issueId + '" width="200" />';
			}
		}
    ],
    listeners : {
		beforeitemdblclick : function(grid, record, item, index, event, options) {
			ms.ImageWindow.show(record.get("innerId"), record.get("issueId"));
	    }
    },
   	bbar: ms.PagingToolbar
});


ms.Panel = Ext.create('Ext.tab.Panel', {
    activeTab: 0,
    defaults : {
        border : false,
        xtype : "panel",
    },
    items: [
        {
            title : "Files",
            layout: {
            	type: "column",
            },
            defaults : {
                xtype : "panel",
                border : false
            },
            items: [
                {
                	items : [ ms.FilesGrid ]
                },
                {
                	id : "files-tab-ms-data-grid",
                	columnWidth: 1,
                	items : [ ms.DataGrid ]
                }
            ],
            listeners : {
                activate : function(tab) {
                	if (ms.FilesStore.count() <= 0) {
                		ms.FilesStore.load();
                	}
                	var container = Ext.getCmp("files-tab-ms-data-grid");
                    if (container.items.length == 0) {
                    	container.add(ms.DataGrid);

                    	ms.PagingToolbar.bindStore(ms.DataStore);
                    	ms.DataGrid.reconfigure(ms.DataStore);
//                    	ms.DataStore.reload();
                    }
                }
            }
        },
        {
            title: "Peaks",
            listeners : {
                activate : function(tab) {
                    if (tab.items.length == 0) {
                        tab.add(ms.DataGrid);

                        ms.PagingToolbar.bindStore(ms.PeaksStore);
                        ms.DataGrid.reconfigure(ms.PeaksStore);
                        ms.PeaksStore.reload();
                    }
                }
            }
        }
    ]
});