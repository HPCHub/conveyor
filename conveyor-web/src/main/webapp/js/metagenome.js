var metagenome = {};


metagenome.TreeStore = Ext.create('Ext.data.TreeStore', {
    root: {
        expanded: true
    },
    autoLoad : false,
    fields : [ "projectId", "name" ]
});


metagenome.Tree = Ext.create('Ext.tree.Panel', {
    width : 300,
    store : metagenome.TreeStore,
    multiSelect : false,
    displayField : "name",
    rootVisible : false,
    buttons: [
        {
            text: 'submit',
            listeners : {
                click : function() {
                    var nodes   = metagenome.Tree.getView().getChecked();
                    var runs    = new Array();
                    var samples = new Array();

                    Ext.Array.each(nodes, function(node) {
                        if (node.isLeaf()) {
                            samples.push(node.get('name'));
                        } else {
                            runs.push(node.get('id'));
                        }
                    });
console.log([samples, runs]);
                    var url = 'http://localhost:3838/metagenome/';
                    if (runs.length > 0) {
                        url = Ext.urlAppend(url, "runs=" + runs.join(','));
                    } if (samples.length > 0) {
                        url = Ext.urlAppend(url, "samples=" + samples.join(',')); 
                    }

                    document.getElementById('metagenome-frame').src = url;
//                    alert(document.getElementById('metagenome-frame').contentWindow.location);
//                    Ext.MessageBox.show({
//                        title: 'Selected Nodes',
//                        msg: samples.join(','),
//                        icon: Ext.MessageBox.INFO
//                    });
                }
            }
        }
    ],
    listeners : {
	    afterrender : function() {
	    	Ext.Ajax.request({
	    	    url: '/project/list',
	    	    success: function(response) {
	    	        Ext.each(Ext.JSON.decode(response.responseText).projects, function(project) {
	    	        	metagenome.Tree.getRootNode().appendChild({
	    					name : project.name,
	    					id : project.name,
	    					expandable : true,
	    					leaf : false,
	    					hrefTarget : "/run/list?projectId=" + project.projectId
	    				});
	    	        })
	    	    }
	    	});
	    },
    	itemexpand : function(node, eOpts) {
    		if (! node.hasChildNodes()) {
		    	Ext.Ajax.request({
		    	    url: node.raw.hrefTarget,
		    	    success: function(response) {
		    	    	var json = Ext.JSON.decode(response.responseText);
		    	    	if (json.runs) {
			    	        Ext.each(json.runs, function(run) {
			    	        	node.appendChild({
			    					name : run.name,
			    					id : run.runId,
			    					runId : run.runId,
			    					checked : false,
			    					expandable : true,
			    					leaf : false,
			    					hrefTarget : "/readSet/list?runId=" + run.runId
			    				});
			    	        })
		    	    	} else if (json.files) {
			    	        Ext.each(json.files, function(file) {
			    	        	node.appendChild({
			    					name : file.seqsSample.name,
			    					id : file.seqsSample.sampleId,
			    					checked : false,
			    					expandable : true,
			    					leaf : true
			    				});
			    	        })
		    	    	}
		    	    }
		    	});
    		}
    	}
    }
});


metagenome.Panel = Ext.create('Ext.panel.Panel', {
    layout : "hbox",
    pack: 'start',
    align: 'stretch',
    items : [
        metagenome.Tree,
        {
        	id : 'metagenome.Frame',
            xtype : "panel",
            flex : 1,
            html : '<iframe id="metagenome-frame" src="http://localhost:3838/metagenome/" width="100%" height="100%">Your browser sucks</iframe>'
        }
    ]
})