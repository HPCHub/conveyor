MassUpload = {};


MassUpload.Store = Ext.create("Ext.data.Store", {
	storeId : "MassUpload.Store",
	fields : [ "fileName", "isFileExists", "isFileLoaded", "readSetId", "sampleName", "sampleId", "runName", "runId", "projectName", "projectId", "sequencerName", "sequencerId" ]
});


MassUpload.Grid = Ext.create("Ext.grid.Panel", {
	layout : "fit",
	flex : 1,
	border: false,
	header : false,
	autoScroll : true,
	store : MassUpload.Store,
	columns : [ {
		width: 35,
		dataIndex : "isFileLoaded",
		renderer : function(isFileLoaded) {
			if (isFileLoaded === null) {
				return '<img src="/js/extjs-4.1.1/resources/themes/images/default/tree/loading.gif" />';
			} else if (isFileLoaded) {
				return '<img src="/js/extjs-4.1.1/resources/themes/images/default/tree/drop-yes.gif" />';
			} else {
				return '<img src="/js/extjs-4.1.1/resources/themes/images/default/tree/drop-no.gif" />';
			}
		}
	}, {
		text : "fileName",
		dataIndex : "fileName",
		flex : true,
		renderer : function(fileName, metaData, record, rowIndex, colIndex, store, gridView) {
			if (record.data.isFileExists) {
				metaData.style += " background-color : #b7ffc6";
			} else {
				metaData.style += " background-color : #ffb7b7";
			}

			return fileName;
		}
	}, {
		text : "readSetId",
		dataIndex : "readSetId",
		renderer : function(readSetId) {
			return parseInt(readSetId);
		}
	}, {
		text : "sampleName",
		dataIndex : "sampleName",
		renderer : function(sampleName, metaData, record, rowIndex, colIndex, store, gridView) {
			if (record.data.sampleId) {
				metaData.style += " background-color : #b7ffc6";
			} else {
				metaData.style += " background-color : #ffb7b7";
			}

			return sampleName;
		}
	}, {
		text : "runName",
		dataIndex : "runName",
		renderer : function(runName, metaData, record, rowIndex, colIndex, store, gridView) {
			if (record.data.runId) {
				metaData.style += " background-color : #b7ffc6";
			} else {
				metaData.style += " background-color : #ffb7b7";
			}

			return runName;
		}
	}, {
		text : "projectName",
		dataIndex : "projectName",
		renderer : function(projectName, metaData, record, rowIndex, colIndex, store, gridView) {
			if (record.data.projectId) {
				metaData.style += " background-color : #b7ffc6";
			} else {
				metaData.style += " background-color : #ffb7b7";
			}

			return projectName;
		}
	}, {
		text : "sequencer",
		dataIndex : "sequencerName",
		renderer : function(sequencerName, metaData, record, rowIndex, colIndex, store, gridView) {
			if (record.data.sequencerId) {
				metaData.style += " background-color : #b7ffc6";
			} else {
				metaData.style += " background-color : #ffb7b7";
			}

			return sequencerName;
		}
	}],
	buttons: [
	    {
	    	text : "Save",
	    	listeners : {
	    		click : function() {
	    			MassUpload.Store.each(function(record) {
	    				if (record.get("isFileLoaded") || ! record.get("isFileExists") || ! record.get("sequencerId")) {
	    					return;
	    				}

	    				record.set("isFileLoaded", null);

	    				/*
	    				 * Create new sample instance if it is not exists and update grid store.
	    				 */
	    				if (! record.get("sampleId")) {
	    					console.log("Create new sample [" + record.get("sampleName") + "]");
	    	            	Ext.Ajax.request({
	    	            	    url: '/sample/save',
	    	            	    async : false,
	    	            	    params: {
	    	            	    	name : record.get("sampleName")
	    	            	    },
	    	            	    success: function(response) {
	    	            	    	var sample = Ext.JSON.decode(response.responseText);
	    		    				var index = -1;
	    	            	    	while (match = MassUpload.Store.findRecord("sampleName", record.get("sampleName"), index + 1, false, true, true)) {
	    	            	    		index = MassUpload.Store.indexOf(match);
	    	            	    		match.set("sampleId", sample.sampleId);
	    	            	    	}
	    	            	    },
            	    			failure : function() {
            	    				return;
            	    			}
	    	            	});
	    				}

	    				/*
	    				 * Create new project instance if it is not exists and update grid store.
	    				 */
	    				if (! record.get("projectId")) {
	    					console.log("Create new project [" + record.get("projectName") + "]");
	    	            	Ext.Ajax.request({
	    	            	    url: '/project/save',
	    	            	    async : false,
	    	            	    params: {
	    	            	    	name : record.get("projectName")
	    	            	    },
	    	            	    success: function(response) {
	    	            	    	var project = Ext.JSON.decode(response.responseText);
	    		    				var index = -1;
	    	            	    	while (match = MassUpload.Store.findRecord("projectName", record.get("projectName"), index + 1, false, true, true)) {
	    	            	    		index = MassUpload.Store.indexOf(match);
	    	            	    		match.set("projectId", project.projectId);
	    	            	    	}
	    	            	    },
            	    			failure : function() {
            	    				return;
            	    			}
	    	            	});
	    				}

	    				/*
	    				 * Create new run instance if it is not exists and update grid store.
	    				 */
	    				if (! record.get("runId")) {
	    					console.log("Create new run [" + [record.get("runName"), record.get("sequencerId"), record.get("projectName")] + "]");
	    	            	Ext.Ajax.request({
	    	            	    url: '/run/save',
	    	            	    async : false,
	    	            	    params: {
	    	            	    	name : record.get("runName"),
	    	            	    	sequencerId : record.get("sequencerId"),
	    	            	    	projectId : record.get("projectId")
	    	            	    },
	    	            	    success: function(response) {
	    	            	    	var run = Ext.JSON.decode(response.responseText);
	    		    				var index = -1;
	    	            	    	while (match = MassUpload.Store.findRecord("runName", record.get("runName"), index + 1, false, true, true)) {
	    	            	    		index = MassUpload.Store.indexOf(match);
	    	            	    		match.set("runId", run.runId);
	    	            	    	}
	    	            	    },
            	    			failure : function() {
            	    				return;
            	    			}
	    	            	});
	    				}

	    				/*
	    				 * Save sequence.
	    				 */
    	            	Ext.Ajax.request({
    	            	    url: "/readSet/save",
    	            	    async : false,
    	            	    params: {
    		    				useFileSystem : true,
    		    				readSetPath : record.get("fileName"),
    		    				runId : record.get("runId"),
    		    				sampleId : record.get("sampleId")
    	            	    },
    	            	    success: function(response) {
    	            	    	var readSet = Ext.JSON.decode(response.responseText);
    	            	    	if (readSet.readSetId) {
    	            	    		record.set("isFileExists", true);
    	            	    		
    	            	    	}
    	            	    },
        	    			failure : function() {
        	    				return;
        	    			}
    	            	});

    	            	record.set("isFileLoaded", true);
	    			})

	    			Ext.Msg.alert("System message", "Files succefully uploaded");
	    		}
	    	}
		}
    ]
});


MassUpload.Form = Ext.create("Ext.form.Panel", {
	bodyPadding : 5,
	url : "/file/massUpload",
	border : false,
	defaults : {
		anchor : "100%"
	},
	items : [{
		xtype : "filefield",
		name : "importFile",
		fieldLabel : "file",
		allowBlank : false
	}],
	buttons : [{
		text : "Submit",
		width : 97,
		formBind : true,
		disabled : true,
		handler : function() {
			var form = this.up("form").getForm();
			if (form.isValid()) {
				form.submit({
					success : function(form, action) {
					},
					failure : function(form, action) {
						MassUpload.Store.loadData([], false);
						var table = Ext.JSON.decode(action.response.responseText).table;
						var rows = [];
						for (var i in table) {
							rows.push({
								fileName : table[i][0],
								isFileExists : table[i][1],
								isFileLoaded : table[i][2],
								readSetId : table[i][3],
								sampleName : table[i][4],
								sampleId : table[i][5],
								runName : table[i][6],
								runId : table[i][7],
								projectName : table[i][8],
								projectId : table[i][9],
								sequencerName : table[i][10],
								sequencerId : table[i][11]
							});
						}
						MassUpload.Store.add(rows);
					}
				});
			}
		}
	}]
});


MassUpload.Panel = Ext.create('Ext.panel.Panel', {
	border : false,
	layout: {
        type: 'vbox',
        align : 'stretch',
        pack  : 'start'
    },
    items : [
		MassUpload.Form,
	    MassUpload.Grid
    ]
})