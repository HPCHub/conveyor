xcms = {};


xcms.DatasetStore = Ext.create('Ext.data.Store', {
	fields:["xcmsDataSetId", "name"],
    autoLoad : false,
	proxy: {
		type : 'ajax',
	    url: '/xcms/list',
	    reader: {
	    	type : 'json',
	        root: 'dataSets',
	        totalProperty: 'totalCount'
	    }
	}
});


xcms.DatasetGrid = Ext.create('Ext.grid.Panel', {
    header: false,
    loadMask: true,
    flex : 1,
    store: xcms.DatasetStore,
    columns: [{
    	text: 'dataSet',
    	dataIndex: 'name',
    	flex: true
	}],
	listeners : {
		select : function(grid, record, index) {
			xcms.IssuesStore.load();
		}
	}
});


xcms.IssuesStore = Ext.create('Ext.data.Store', {
	fields:[ "xcmsIssueId", "name", "foldChange", "rtMed", "mzMed", "PValue", "QValue", "FScore" ],
	remoteSort : true,
    autoLoad : false,
	proxy: {
		type : 'ajax',
	    url: '/xcms/issues',
	    reader: {
	    	type : 'json',
	        root: 'issues',
	        totalProperty: 'totalCount'
	    }
	},
	listeners : {
		beforeload: function(store, operation) {
			if (xcms.DatasetGrid.getView().getSelectionModel().getSelection()[0]) {
				operation.params = {
					xcmsDataSetId: xcms.DatasetGrid.getView().getSelectionModel().getSelection()[0].get("xcmsDataSetId")
				}
			}
		}
	}
});


xcms.IssuesGrid = Ext.create('Ext.grid.Panel', {
	flex : 2,
    header: false,
    loadMask: true,
    store: xcms.IssuesStore,
    columns: [
        {
	    	text: "name",
	    	dataIndex: "name",
	    	flex: true
	    }, {
	    	text: "foldChange",
	    	dataIndex: "foldChange",
	    }, {
	    	text: "rtMed",
	    	dataIndex: "rtMed",
	    }, {
	    	text: "mzMed",
	    	dataIndex: "mzMed",
	    }, {
	    	text: "PValue",
	    	dataIndex: "PValue",
	    }, {
	    	text: "QValue",
	    	dataIndex: "QValue",
	    }, {
	    	text: "FScore",
	    	dataIndex: "FScore",
	    }
    ],
	listeners : {
		deselect: function(selection, row) {
			xcms.search();
        },
        select: function(selection, row) {
        	xcms.search();
        }
	},
	bbar: Ext.create('Ext.PagingToolbar', {
        store: xcms.IssuesStore,
        displayInfo: true,
        displayMsg: 'Displaying ms {0} - {1} of {2}',
        emptyMsg: "No ms to display"
    })
});


xcms.FilesGrid = Ext.create('Ext.grid.Panel', {
	flex : 1,
    header: false,
    loadMask: true,
    store: ms.FilesStore,
    selModel : Ext.create('Ext.selection.CheckboxModel'),
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
    		xcms.search();
    	},
    	deselect : function(grid, record, index) {
    		xcms.search();
    	}
    }
});


xcms.search = function() {

	var filesModel = xcms.FilesGrid.getSelectionModel().getSelection();
	if (filesModel.length < 1) {
		return;
	}

	var issuesModel = xcms.IssuesGrid.getSelectionModel().getSelection();
	if (issuesModel.length < 1) {
		return;
	}

	xcms.DataStore.load({
		params : {
			issues : ModelUtil.toArray(issuesModel, "xcmsIssueId"),
			files : ModelUtil.toArray(filesModel, "fileId")
		}
	});
}


xcms.FilePanel = new Ext.panel.Panel({
	border : false,
	height : 300,
	layout : "hbox",
	items : [
         xcms.DatasetGrid,
         xcms.IssuesGrid,
         xcms.FilesGrid
	],
	listeners : {
		render : function() {
			xcms.DatasetGrid.setHeight(300);
			xcms.IssuesGrid.setHeight(300);
			xcms.FilesGrid.setHeight(300);
		}
	}
});


xcms.DataStore = Ext.create('Ext.data.Store', {
	fields:["isCredible", "innerId", "issueId", "msIon", "name", "precursor", "precursorPpm", "score"],
	proxy: {
		type : 'ajax',
	    url: '/ms/search',
	    reader: {
	    	type : 'json',
	        root: 'msIons',
	        totalProperty: 'totalCount'
	    }
	}
});


xcms.DataGrid = Ext.create('Ext.grid.Panel', {
    header: false,
    loadMask: true,
    autoScroll: true,
    overflowY: "scroll",
    scroll : "vertical",
    store: xcms.DataStore,
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
	            	    	issueId: xcms.DataGrid.getStore().getAt(rowIndex).get("issueId")
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
			width: 220,
			renderer : function(innerId) {
				return '<img src="http://metlin.scripps.edu/Mol_images/' + innerId + '.png" width="200" />';
			}
		},
		{
			text: 'spectrumMatching',
			dataIndex: "issueId",
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
   	bbar: Ext.create('Ext.PagingToolbar', {
        store: ms.DataStore,
        displayInfo: true,
        displayMsg: 'Displaying ms {0} - {1} of {2}',
        emptyMsg: "No ms to display"
    })
});


xcms.Panel = new Ext.panel.Panel({
	layout: {
    	type: 'vbox',
    	align: 'stretch'
	},
	defaults : {
		border : false
	},
    items: [
        xcms.FilePanel,
        xcms.DataGrid
    ]
});