var conveyor = {};


conveyor.Panel = Ext.create('Ext.tab.Panel', {
	id : "conveyor-tab-panel",
    layout: "fit",
    activeTab: 3,
    defaults : {
    	layout: "fit"
    },
    items: [
        {
            title: 'Projects',
            items: [ projectsTab ],
            listeners: {
       	        activate: function(panel) {
       	        	Ext.getCmp("projects-tab-projects-grid").add(projectsGrid);

       	            viewport = Ext.ComponentQuery.query('viewport')[0];
       	            projectsTab.setHeight(viewport.getHeight() - 35);

       	            Ext.getCmp("projects-tab-runs-grid").add(runsGrid);
       	            runs.Functions.reloadByProjectsGrid.call()

                    Ext.getCmp("projects-tab-files-grid").add(filesGrid);
       	        },
            	render: function() {
            		projectStore.loadPage(1);
            	}
            }
        },
        {
            title: 'Runs1301',
            items: [runsTab],
            listeners: {
                activate: function(tab) {
                	var runsGridContainer = Ext.getCmp("runs-center-grid");
                	runsGridContainer.add(runsGrid)

                	viewport = Ext.ComponentQuery.query('viewport')[0];
                    runsTab.setHeight(viewport.getHeight() - 35);

                    Ext.getCmp("files-east-grid").add(filesGrid);

                    var selection = runsGrid.getView().getSelectionModel().getSelection()[0];
                    if (selection) {
                        filesStore.load({
                            params: {runId: selection.get("runId")}
                        });
                    }
                    sequencersStore.load();
                	runs.Store.DataStore.load({
                        params: {}
                    });
                },
                render: function() {
                    runs.Store.DataStore.loadPage(1);
                }
            }
        },
        {
            title: 'Samples',
            items: [ samplesGrid ],
	        listeners: {
                activate: function(tab) {
                    viewport = Ext.ComponentQuery.query('viewport')[0];
                },
	            render: function() {
	                samples.Store.DataStore.loadPage(1);
	            }
	        }
        },
        {
            title: 'Read sets',
            items: [ filesGrid ],
            listeners: {
                activate: function(tab) {
                    tab.add(filesGrid);
                    filesStore.load({
                        params: {}
                    });
                }
//                render: function() {
//                	filesStore.loadPage(1);
//                }
            }
        },
        {
            title: 'References',
            items: [ ReferencesGrid ],
            listeners: {
                activate: function(tab) {
                    ReferencesStore.load({
                        params: {}
                    });
                },
                render: function() {
                    ReferencesStore.loadPage(1);
                }
            }
        },
        {
            title: 'Analysis',
            items: [ analysis.AnalysisGrid ],
            listeners : {
            	activate : function(tab) {
            		analysis.AnalysisStore.load();
            	}
            }
        },
        {
            title : "MassUpload",
            items : [ MassUpload.Panel ]
        }
    ]
});


conveyor.popup = {
	window : null,
	_closeHandler : null,
	getWindow : function() {
		if (this.window == null) {
			this.window = new Ext.Window({
			    layout      : 'fit',
			    closeAction :'hide',
			    plain       : true,
			    listeners : {
			        close: function() {
			            this.child('form').getForm().reset();
			            if (_closeHandler) {
			            	_closeHandler.call()
			            }
			        }
			    }
			})
		}

		this.window.setHeight(Ext.getBody().getHeight() * 0.8);
		this.window.setWidth(Ext.getBody().getWidth() * 0.8);

		return this.window;
	},

	show : function(form, title, closeHandler) {
		_closeHandler = closeHandler || new Function()
		var window = this.getWindow();
		window.removeAll(false);
		window.add(form);
		window.setTitle(title);
		window.show();
	},

    hide : function(isReset) {
        this.getWindow().hide();
        if (isReset) {
            this.window.child('form').getForm().reset();
        }
    }
};