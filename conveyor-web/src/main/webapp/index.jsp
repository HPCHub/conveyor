<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>CONVEYOR</title>
<link rel="stylesheet" type="text/css" href="/js/extjs-4.1.1/resources/css/ext-all.css">
<link rel="stylesheet" type="text/css" href="/js/extjs-4.1.1/ux/css/CheckHeader.css">
<script type="text/javascript" src="/js/extjs-4.1.1/ext-all-dev.js"></script>
<script type="text/javascript" src="/js/extjs-4.1.1/ux/CheckColumn.js"></script>
<script type="text/javascript" src="/js/explorer.js"></script>
<script type="text/javascript" src="/js/util.js"></script>
<script type="text/javascript" src="/js/widgets.js"></script>
<script type="text/javascript" src="/js/projects.js"></script>
<script type="text/javascript" src="/js/samples.js"></script>
<script type="text/javascript" src="/js/read_sets.js"></script>
<script type="text/javascript" src="/js/runs.js"></script>
<script type="text/javascript" src="/js/references.js"></script>
<script type="text/javascript" src="/js/analysis.js"></script>
<script type="text/javascript" src="/js/mass_upload.js"></script>
<script type="text/javascript" src="/js/conveyor.js"></script>
<script type="text/javascript" src="/js/ms.js"></script>
<script type="text/javascript" src="/js/xcms.js"></script>
<script type="text/javascript" src="/js/metagenome.js"></script>
<style type="text/css">
.x-btn-default-small .x-btn-inner {
    font-size: 20px;
}
.x-grid-cell {
    vertical-align: middle;
}
</style>
<script>
Ext.application({
    name : 'Conveyor',
    launch : function() {
        Ext.create('Ext.container.Viewport', {
            layout : 'fit',
            items : [ 
                {
	                xtype: "panel",
	                border: false,
	                layout: 'border',
	                defaults: {
	                    border: false,
	                    header: false,
	                    collapsible: true,
	                    split: true
	                },
	                items: [ 
	                    {
	                        region: "west",
	                        layout: "vbox",
	                        width : 200,
	                        header: true,
	                        title : "sections",
	                        defaultType : "button",
	                        defaults : {
	                        	height : 30,
	                        	width : 200
	                        },
	                        items : [
	                            {
	                            	text : "conveyor",
	                            	handler: function() {
	                            		var contentPanel = Ext.getCmp("viewport-content");
	                            		contentPanel.removeAll(false);
	                            		contentPanel.add(conveyor.Panel);
	                                }
	                            },
	                            {
	                            	text : "ms",
	                            	handler: function() {
	                            		var contentPanel = Ext.getCmp("viewport-content");
	                            		contentPanel.removeAll(false);
	                            		contentPanel.add(ms.Panel);
                                    }
	                            },
// 	                            {
//                                     text : "xcms",
//                                     handler: function() {
//                                         var contentPanel = Ext.getCmp("viewport-content");
//                                         contentPanel.removeAll(false);
//                                         contentPanel.add(xcms.Panel);
//                                     }
//                                 },
                                {
                                    text : "metagenome",
                                    handler: function() {
                                        var contentPanel = Ext.getCmp("viewport-content");
                                        contentPanel.removeAll(false);
                                        contentPanel.add(metagenome.Panel);
                                    }
                                }
                            ]
	                    },
	                    {
	                    	layout: "fit",
	                        id: "viewport-content",
	                        region: "center",
	                        flex: 1,
	                        items : [ conveyor.Panel ]
	                    }
	                ]
                }
            ],
            listeners : {
            	resize: function(viewport) {
            		var height = viewport.getHeight() - 35;
            		projectsTab.setHeight(height);
            		projectsGrid.setHeight(height);
            		runsGrid.setHeight(height);
            		runsTab.setHeight(height);
            		samplesGrid.setHeight(height);
            		filesGrid.setHeight(height);
            		ReferencesGrid.setHeight(height);
            		ms.DataGrid.setHeight(height);
            		ms.FilesGrid.setHeight(height);
            		analysis.AnalysisGrid.setHeight(height);
            		metagenome.Tree.setHeight(height);
            		Ext.getCmp('metagenome.Frame').setHeight(height);
                }
            }
        });
    }
});
</script>
</head>
<body></body>
</html>