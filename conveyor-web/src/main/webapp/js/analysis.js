var analysis = {};


analysis.AnalysisStore = new Ext.data.JsonStore({
	fields : ["readSetId", "tag", "COG", "GO", "genus", "org"],
	autoLoad : false,
	remoteSort : true,
	pageSize : 45,
	proxy : {
		type : "ajax",
		url : "/analysis/list",
		reader : {
			type : "json",
			root : "analyses",
			totalProperty : "totalCount"
		}
	}
});


analysis.Types = {};
analysis.Types.cog   = new Array();
analysis.Types.go    = new Array();
analysis.Types.genus = new Array();
analysis.Types.org   = new Array();
analysis.checkAnalyse = function(type, readSetId, checked) {
	switch (type) {
		case "COG": {
			var idList = analysis.Types.cog;
		} break;
		case "GO": {
			var idList = analysis.Types.go;
		} break;
		case "genus": {
			var idList = analysis.Types.genus;
		} break;
		case "org": {
			var idList = analysis.Types.org;
		} break;
	}

	if (checked) {
		idList.push(readSetId);
	} else {
		Ext.Array.remove(idList, readSetId);
	}
}


analysis.Store = new Ext.data.Store({
    fields: ["column"]
});


analysis.form = {}


//analysis.form.isFormComplete = function() {
//
//	var isComplete = true;
//	analysis.form.Form.getForm().getFields().each(function(item) {
//		if (! item.is("combobox")) {
//			return;
//		}
//
//		if (item.isHidden()) {
//			return;
//		}
//
//		if (item.getRawValue() == "") {
//			isComplete = false;
//		}
//	});
//
//	console.log("isComplete: " + isComplete);
//	return isComplete;
//}


analysis.form.successHandler = function(form, action) {

	analysis.form.Form.getForm().getFields().each(function(item) {
		if (item.isHidden()) {
			item.show();
		} if (item.is("checkbox")) {
			item.setValue("on");
		}
	});

	analysis.Store.removeAll();
	with (action.result) {
		for (i in columns) {
			analysis.Store.add({column : columns[i]});
		}
	}
}


analysis.form.Form = new Ext.form.Panel({
	border : false,
	bodyPadding : 5,
	url : "/analysis/process",
	layout : "anchor",
	defaults : {
		anchor : "100%"
	},
	defaultType : "checkbox",
	items : [
		{
			xtype : "filefield",
		 	name : "metafile",
			fieldLabel : "metafile",
			allowBlank : true,
			listeners : {
				change : function(filefield, value, eOpts) {
					analysis.form.Form.submit({
						success: analysis.form.successHandler,
						params : {
							metafileProcessing : true
						},
					    failure: function(form, action) {
					    	alert("FAIL");
					    }
					});
				}
			}
		},
	    {
			xtype : "panel",
			layout: "hbox",
			border: 0,
			items : [
	         	{	
	         		xtype: "checkbox",
	         		name : "MDS",
	         		checked : true,
					fieldLabel : "MDS",
				}, {
					xtype: "combo",
					hidden : true,
					name : "colorBy",
					fieldLabel : "color by",
					store : analysis.Store,
					displayField : "column",
					valueField : "column",
					margin: '0 0 0 30px',
					labelWidth: 60,
					queryMode: "local",
				    triggerAction: "all",
				    lastQuery: "",
					listeners: {
					    beforequery: function(qe) {
					        delete qe.combo.lastQuery;
					    }
					}
				}, {
					xtype: "combo",
					hidden : true,
					name : "shapeBy",
					fieldLabel : "shape by",
					store : analysis.Store,
					displayField : "column",
					valueField : "column",
					margin: '0 0 0 30px',
					labelWidth: 60,
					queryMode: "local",
				    triggerAction: "all",
				    lastQuery: "",
					listeners: {
					    beforequery: function(qe) {
					        delete qe.combo.lastQuery;
					    }
					}
				}
	         ]
		}, {
	     	name : "heatplot",
	    	fieldLabel : "Heatplot",
	    	checked : true,
		}, {
			xtype : "panel",
			layout: "hbox",
			border: false,
			items : [
	         	{
	         		xtype: "checkbox",
	         		name : "randomForest",
	         		hidden : true,
	     			fieldLabel : "Random Forest"
	         	}, {
	         		flex : true,
					xtype: "combo",
					hidden : true,
					name : "randomForestValues",
					fieldLabel : "values",
					multiSelect : true,
					store : analysis.Store,
					displayField : "column",
					valueField : "column",
					margin: '0 0 0 30px',
					labelWidth: 60,
					queryMode: "local",
				    triggerAction: "all",
				    lastQuery: "",
					listeners: {
					    beforequery: function(qe) {
					        delete qe.combo.lastQuery;
					    }
					}
				}
	         ]
		}, {
			xtype : "panel",
			layout: "hbox",
			border: false,
			items : [
	         	{
	         		xtype: "checkbox",
	         		name : "utest",
	     			fieldLabel : "U-Test",
	     			hidden : true,
	         	}, {
	         		flex : true,
	         		hidden : true,
					xtype: "combo",
					name : "utestValues",
					fieldLabel : "values",
					multiSelect : true,
					store : analysis.Store,
					displayField : "column",
					valueField : "column",
					margin: '0 0 0 30px',
					labelWidth: 60,
					queryMode: "local",
				    triggerAction: "all",
				    lastQuery: "",
					listeners: {
					    beforequery: function(qe) {
					        delete qe.combo.lastQuery;
					    }
					}
	         	}
	         ]
		}, {
	     	name : "cluster",
	    	fieldLabel : "Cluster",
	    	checked : true,
		}
	],
	buttons : [{
		text : "Submit",
		formBind : true,
		disabled : true,
		handler : function() {
			var form = this.up("form").getForm();
			if (form.isValid()) {
				form.submit({
					success : analysis.form.successHandler,
					failure : function(form, action) {
						Ext.Msg.alert("Failed", action.result.msg);
					}
				});
			}
		}
	}]
});


analysis.AnalysisGrid = new Ext.grid.Panel({
	border: false,
	header : false,
	loadMask : true,
	store : analysis.AnalysisStore,
	columns : [{
		text : "id",
		dataIndex : "readSetId"
	}, {
		text : "read set name",
		flex : true,
		dataIndex : "tag"
	}, {
		text : "COG",
		align : "center",
		dataIndex : "COG",
		xtype: "checkcolumn",
		listeners : {
			checkchange : function(column, rowIndex, checked) {
				analysis.checkAnalyse("COG", analysis.AnalysisGrid.getStore().getAt(rowIndex).get("readSetId"), checked);
			}
		}
	}, {
		text : "GO",
		align : "center",
		dataIndex : "GO",
		xtype: "checkcolumn",
		listeners : {
			checkchange : function(column, rowIndex, checked) {
				analysis.checkAnalyse("GO", analysis.AnalysisGrid.getStore().getAt(rowIndex).get("readSetId"), checked);
			}
		}
	}, {
		text : "genus",
		align : "center",
		dataIndex : "genus",
		xtype: "checkcolumn",
		listeners : {
			checkchange : function(column, rowIndex, checked) {
				analysis.checkAnalyse("genus", analysis.AnalysisGrid.getStore().getAt(rowIndex).get("readSetId"), checked);
			}
		}
	}, {
		text : "org",
		align : "center",
		dataIndex : "org",
		xtype: "checkcolumn",
		listeners : {
			checkchange : function(column, rowIndex, checked) {
				analysis.checkAnalyse("org", analysis.AnalysisGrid.getStore().getAt(rowIndex).get("readSetId"), checked);
			}
		}
	}],
	bbar : new Ext.PagingToolbar({
		store : analysis.AnalysisStore,
		displayInfo : true,
		displayMsg : "Displaying files {0} - {1} of {2}",
		emptyMsg : "No files to display",
		items : [{
		    xtype : "button",
		    text : "Coverage",
		    href : "/analysis/coverage",
		    hrefTarget: "_blank",
		    listeners : {
		    	click : function() {
		    		this.setParams({
            	    	cogReadSet : analysis.Types.cog,
		            	goReadSet : analysis.Types.go,
		            	genusReadSet : analysis.Types.genus,
		            	orgReadSet : analysis.Types.org
            	    })
		    	}
		    }
		}, {
		    xtype : "button",
		    text : "Stats",
		    href : "/analysis/stats",
		    hrefTarget: "_blank",
		    listeners : {
		    	click : function() {
		    		this.setParams({
            	    	cogReadSet : analysis.Types.cog,
		            	goReadSet : analysis.Types.go,
		            	genusReadSet : analysis.Types.genus,
		            	orgReadSet : analysis.Types.org
            	    })
		    	}
		    }
		}, {
		    xtype : "button",
		    text : "Analyse",
		    listeners : {
		    	click : function() {
		    		conveyor.popup.show(analysis.form.Form);
		    	}
		    }
		}]
	})
});