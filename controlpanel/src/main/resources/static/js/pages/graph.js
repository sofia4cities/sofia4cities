var GraphController = function() {
    
	// DEFAULT PARAMETERS, VAR, CONSTS. 
    var APPNAME  = 'Sofia4Cities Control Panel'
	, LIB_TITLE  = 'Graph Controller'
    , logControl = 0;
	
	// GRAPH INITIALIZATION VARS AND CONST.
	var links 			 = []
	,	nodes 			 = []
	,	data 			 = []
	,	edges 			 = []
	,	ArraySource 	 = []
	,	jsonProjects	 = {}
	,	options 		 = {}
	,	jsonIndex 		 = {}
	,	graphData		 = { nodes: [], edges: []}
	,	containerNetwork = new Object()
	,	network 		 = null;
	
	
	// GRAPH OPTIONS DEFAULTS
	options = {	
		manipulation: false,		
		interaction: { dragNodes: true, dragView: true, hover:true , navigationButtons: true,keyboard: true},		
		physics: {
		    forceAtlas2Based: { gravitationalConstant: -70 },
		    solver: "forceAtlas2Based"
		},
		nodes: {
			shadow: false,		  
			borderWidth:4,
			size:30,
			scaling:{
				label: { min:10, max:20 }
			},
			color:{ border: '#222222', background: '#666666' },
			font: { color:'#666'}
        },
        edges: {
			arrows: {
				to: {enabled: true, scaleFactor: 1 }
			},
			font: { align: 'middle', size: 10 },
			shadow: false,
			smooth: true,
			labelHighlightBold: true,
			color: '#b5afaf'
		},
		autoResize : true,
		width: '100%',
		height : '475px',
		groups:{
			resolved :{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf2d2', size: 30, color: '#ed6b75' }
			},
			licensing:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf2d0', size: 30, color: '#c49f47' }
			},
			usuario:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf2be', size: 60, color: '#4B77BE' }
			},
			gadgets:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf1e6',	size: 40, color: '#525e64' }
			},
			visualizacion:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf080',	size: 50, color: '#525e64' }
			},
			analytics:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf0c3',	size: 50, color: '#525e64' }
			},
			kps:{
				shape: 'icon',
				icon: {	face: 'FontAwesome', code: '\uf2db', size: 40, color: '#5e738b' }
			},
			ontologias:{
				shape: 'icon',
				icon:{ 	face: 'FontAwesome', code: '\uf0e8', size: 40, color: '#5e738b' }
			},
			notebooks:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf02d', size: 40, color: '#5e738b' }
			},
			dashboards:{
				shape: 'icon',
				icon:{ face: 'FontAwesome',	code: '\uf0e4', size: 40, color: '#5e738b' }
			},
			scripts:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf0b0', size: 40, color: '#5e738b' }
			},
			proyectos:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf07b', size: 40, color: '#5e738b' }
			},
			pipelines:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf15b',	size: 40, color: '#5e738b' }
			},
			project:{
				shape: 'icon',
				icon:{face: 'FontAwesome', code: '\uf114', size: 30, color: '#c49f47' }
			},
			gadget:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf1fe', size: 30, color: '#c49f47' }
			},
			script:{
				shape: 'icon',
				icon:{ face: 'FontAwesome',	code: '\uf1c9',	size: 30, color: '#5e738b' }
			},
			pipeline:{
				shape: 'icon',
				icon:{ face: 'FontAwesome',	code: '\uf2d0', size: 30, color: '#c49f47' }
			},
			dashboard:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf1fe', size: 30, color: '#c49f47' }
			},
			ontology:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf1e0', size: 30, color: '#c49f47' }
			},
			kp:{
				shape: 'icon',
				icon: { face: 'FontAwesome', code: '\uf10b', size: 35, color: '#c49f47' }
			},
			notebook:{
				shape: 'icon',
				icon: { face: 'FontAwesome', code: '\uf0f2', size: 30, color: '#c49f47'	}
			},
			Clusterproyectos:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf07b', size: 60, color: '#5e738b' }
			},
			Clusterdashboards:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf0e4',	size: 60, color: '#5e738b' }
			},			
			Clusterontologias:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf0e8',	size: 60, color: '#5e738b' }
			},
			Clusternotebooks:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf02d',	size: 60, color: '#5e738b' }
			},
			Clusterpipelines:{
				shape: 'icon',
				icon:{face: 'FontAwesome', code: '\uf15b', size: 60, color: '#5e738b' }
			},
			Clusterkps:{
				shape: 'icon',
				icon: {
					face: 'FontAwesome',
					code: '\uf2db',
					size: 60,
					color: '#5e738b'
				}
			},
			Clusterscripts:{
				shape: 'icon',
				icon:{ face: 'FontAwesome',	code: '\uf0b0',	size: 60, color: '#5e738b' }
			},
			Clustergadgets:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf1e6', size: 60, color: '#525e64' }
			},
			Clusterlicensing:{
				shape: 'icon',
				icon:{ face: 'FontAwesome', code: '\uf2d0', size: 60, color: '#c49f47' }
			},
			Clusterresolved :{
				shape: 'icon',
				icon:{ face: 'FontAwesome',	code: '\uf2d2',	size: 60, color: '#ed6b75' }
			}
		}
	};
	
	
	// CONTROLLER PRIVATE FUNCTIONS 	
	
	
	// CREATE HTML TABLE TO VIEW GRAPH NODE INFO.
	var createGraphInfoTable = function(){	
		logControl ? console.log('createGraphInfoTable() -> ') : '';
		
		var strInfo = '';
		
		// i18 labels 
		var propertyCol	= graphReg.propertyCol
		,	valueCol	= graphReg.valueCol
		,	tableName	= graphReg.tableName
		,	tableSource	= graphReg.tableSource
		,	tableLinkS	= graphReg.tableLinkS
		,	tableLinkC	= graphReg.tableLinkC
		,	tableLinkBtn= graphReg.tableLinkBtn
		 
		
		$("#TableInfoNetwork").hide();
		strInfo	=	'<thead>'
			+'<tr>'
				+'<th class="bg-grey-steel font-grey-gallery"><i class="fa fa-briefcase"></i> '+ propertyCol +'</th>'
				+'<th class="bg-grey-steel font-grey-gallery">'+ valueCol +'</th>'															
			+'</tr>'
		+'</thead>'
		+'<tbody>'
		+'	<tr>'
		+'		<td class="uppercase font-grey-mint">'+ tableName +'</td>'
		+'		<td id="id_nombre" class="long-text"></td>'
		+'	</tr>'
		+'	<tr>'
		+'		<td class="uppercase font-grey-mint"> '+ tableSource +'</td>'
		+'		<td id="id_source" class="long-text"> </td>'
		+'	</tr>'
		+'	<tr id="id_tr_enlaceS">'
		+'		<td class="uppercase font-grey-mint"> '+ tableLinkS +'</td>'
		+'		<td class="long-text"><a id="id_enlaceS" class="btn btn-square btn-sm blue"><i class="fa fa-cube"></i><span> '+ tableLinkBtn +'</span> </a></td>'
		+'	</tr>'
		+'	<tr id="id_tr_enlaceC">'
		+'		<td class="uppercase font-grey-mint">'+ tableLinkC +'</td>'
		+'		<td class="long-text"><a id="id_enlaceC" class="btn btn-square btn-sm blue"><i class="fa fa-cube"></i><span> '+ tableLinkBtn +'</span> </a></td>'
		+'	</tr>';

		strInfo	+='</tbody>';
		$("#TableInfoNetwork").empty();
		$("#TableInfoNetwork").html(strInfo);
	}
	
	// DRAW NODE INFO ON TABLE
	var drawGraphInfo = function(currentNode){
		logControl ? console.log('drawGrafoInfo() -> ') : '';
		
		// null values to ''
		$.each( currentNode ,function( key, value ){
			if( value == null ){
				currentNode[key] = "";
			}
		});
		
		cleanTable(); // clean graph info table
		
		// fill node info to table 
		$("#TableInfoNetwork").show();		
		$("#id_nombre").html(currentNode.label);
		$("#id_source").html(currentNode.nameSource);
		
		if ( currentNode.linkTarget ){
			$("#id_tr_enlaceS").show();
			$("#id_enlaceS").attr("href",currentNode.linkTarget);
		}
		if ( currentNode.linkCreate ){
			$("#id_tr_enlaceC").show();
			$("#id_enlaceC").attr("href",currentNode.linkCreate);
		}
	}
	
	// AUX. CLEAN GRAPH INFO TABLE
	var cleanTable = function(){
		
		$("#id_nombre").html(); $("#id_source").html();		
		if ( $("#id_enlaceS") ) { $("#id_enlaceS").removeAttr("href"); }
		if ( $("#id_enlaceC") ) { $("#id_enlaceC").removeAttr("href"); }
		$("#id_tr_enlaceS,#id_tr_enlaceC").hide();				
	}
		
	// HANDLE HIERARCHICAL OPTION BUTTONS
	var handleHierarchical = function(){		
		logControl ? console.log('handleHierarchical() -> ') : '';
		
		$("#hierarchicalOptions > li > a").each(function(){
			$(this).on('click',function(){
				hierarchicalNetwork(this);
			});
		});
	}

	// HANDLE CLUSTER OPTION BUTTONS
	var handleCluster = function(){		
		logControl ? console.log('handleCluster() -> ') : '';		
		
		$("#clusterOptions > li > a").each(function(){
			$(this).on('click',function(){
				clusterGraph(this);
			});
		});
	}
	
	// HIERARCHICAL GRAPH REDRAW
	var hierarchicalNetwork = function(obj){
		logControl ? console.log('hierarchicalNetwork() -> ') : '';
		
		var direction = $(obj).attr("data-action");
		if( direction != "" ){
		
			destroyNetwork();
			if( direction != "default" ){
				// setting graph option properties.
				options.layout = {};
				options.layout.hierarchical = {};
				options.layout.hierarchical.direction = direction;
				options.layout.hierarchical.sortMethod = 'directed';
			}
			else{ delete options.layout; }
		
			// creating gpraph with this hierarchical configuration data: graphData
			containerNetwork = document.getElementById('networkVis');
			network = new vis.Network( containerNetwork, graphData, options );
			network.on("selectNode", function( params ){
		
				if( params.nodes.length == 1){
					if( network.isCluster(params.nodes[0] ) == true ){ network.openCluster( params.nodes[0] ); }

					// selected node
					var selectedId  = network.getSelection().nodes;
					var currentNode = graphData.nodes.get( selectedId );
					
					// draw node info to graphInfoTable.
					if( currentNode[0] != undefined ){ drawGraphInfo( currentNode[0] ); }
				}

			});
		}
	}
	
	// CLUSTER GRAPH by CONNECTION
	var clusterByConnection = function(){
		logControl ? console.log('clusterByConnection() -> ') : '';
		network.setData( graphData );
		network.clusterByConnection(1)
	}
	
	// CLUSTER GRAPH REDRAW
	var clusterGraph = function(obj){
		logControl ? console.log('clusterGraph() -> ') : '';
		
		var action = $(obj).attr("data-action");
		if( graphData.nodes.length > 0 ){
			if ( action == "source" ){ clusterGraphBySource();} 
			if ( action == "type" )  { clusterGraphByType();  }
		}
	}
	
	// CLUSTER GRAPH by SOURCE
	function clusterGraphBySource(){
		logControl ? console.log('clusterGraphBySource() ->') : '';
		
		network.setData( graphData );
		var clusterOptionsByData;
		var target = '';
		
		for( var i = 0; i < ArraySource.length; i++){
			var idSource = ArraySource[i].id;
			var target	 = ArraySource[i].target;
			var group	 = ArraySource[i].group;
			clusterOptionsByData = {
				
				joinCondition: 		function (childOptions) { return childOptions.category == target; },
				processProperties:  function (clusterOptions, childNodes, childEdges) {
					var totalMass = 0;
					for (var i = 0; i < childNodes.length; i++) {
						totalMass += childNodes[i].mass;
					}
					clusterOptions.mass = totalMass;
					return clusterOptions;
				},
				clusterNodeProperties: {id:'cluster:'+idSource, borderWidth:3, group:'Cluster'+group, 'label':target }
			};
			network.cluster( clusterOptionsByData );
		}
		
	}
	
	// CLUSTER GRAPH by TYPE
	function clusterGraphByType(){
		logControl ? console.log('clusterGraphByType() -> ') : '';
		
		var ArrayType = ['licensing','resolved'];
		network.setData( graphData );
		var clusterOptionsByData;
		for( var i = 0; i < ArrayType.length; i++){
			
			var type = ArrayType[i];
			clusterOptionsByData = {
				joinCondition: function (childOptions) { return childOptions.type == type; },
				processProperties: function (clusterOptions, childNodes, childEdges) {
					var totalMass = 0;
					for (var i = 0; i < childNodes.length; i++) {
						totalMass += childNodes[i].mass;
					}
					clusterOptions.mass = totalMass;
					return clusterOptions;
				},
				clusterNodeProperties: {id:'cluster:'+type, borderWidth:3, group:'Cluster'+type, label:type}
			};
			network.cluster(clusterOptionsByData);
		}
	}
	
	// CLUSTER GRAPH by PROJECT
	function clusterGrafoByProject(){
		logControl ? console.log('clusterGrafoByProject') : '';
		
		network.setData( graphData );
		var clusterOptionsByData;
		$.each( jsonProjects ,function(project,value){
			clusterOptionsByData = {
				joinCondition: function (childOptions) {
					return childOptions.project == project;
				},
				processProperties: function (clusterOptions, childNodes, childEdges) {
					var totalMass = 0;
					for (var i = 0; i < childNodes.length; i++) {
						totalMass += childNodes[i].mass;
					}
					clusterOptions.mass = totalMass;
					return clusterOptions;
				},
				clusterNodeProperties: {id:'cluster:'+project, borderWidth:3, group:'projectCluster', label:project}
			};
			network.cluster( clusterOptionsByData );
		});
	}
	
	// AUX. DESTROY GRAPH
	var destroyNetwork = function(){ if( network !== null){ network.destroy(); network = null; } }
	
	// AUX. ON LOAD GRAPH DATA GET INTO FORMAT.
	var getDataGraph  = function(){
		logControl ? console.log('getDataGraph() -> ') : '';
		
		var graphDataObj 	= { nodes:[], edges:[] }
		,	ArrayNodes 		= []
		,	ArrayEdges 		= []
		,	idRef 			= {}
		,	target 			= ''
		,	nodes			= {}
		,	avoidDuplicates	= [];
		
		if ($("#id_panel_botones")){ $("#id_panel_botones").hide(); }
		$("#networkVis").hide();
		
		// handle data 
		if ( !$.isEmptyObject(links) ){
			
			if ($("#id_panel_botones")){ $("#id_panel_botones").show(); }
			// main node Loop - create nodes with info.
			$.each( links , function( Index, Node ){
				
				if( $.inArray( Node.nameTarget, avoidDuplicates ) === -1 ){
					if( Node.type === undefined ){ Node.type = "";}
					idRef[ Node.target ] = Index;
				
					// node format
					var dataJson = {'id':Index, 'label':Node.nameTarget, 'source':Node.source, 'type':Node.type, 'linkTarget':Node.linkTarget, 'nameSource':Node.nameSource, 'group':Node.classTarget, 'title':Node.title, 'linkCreate': Node.linkCreate };

					group 	= dataJson.group.toLowerCase();
					type	= dataJson.type.toLowerCase();
				
					if		( options.groups[group] !== undefined ){ dataJson.group = group; }
					else if ( options.groups[type]  !== undefined ){ dataJson.group = type; }
					else{ dataJson.group = 'licensing'; }
				
					dataJson.category = Node.source;
					if( Node.type == 'suit'){
						dataJson.category = Node.target;
						ArraySource.push({'id':Index,'target':Node.target,'group':dataJson.group});
					}
					ArrayNodes.push(dataJson);
					avoidDuplicates.push(Node.nameTarget);
				}
				
			});
			
			// creating relationships between nodes.
			$.each( links , function( Index, Node){
				if( ( idRef[Node.source] !== undefined )&&( idRef[Node.target] !== undefined )&&( idRef[Node.source] !== idRef[Node.target] )){
					ArrayEdges.push({from:idRef[Node.source], to:idRef[Node.target]});
				}else{
					//console.log("Verificar nodo:"+Index+' - Source:'+Node.source+' Target:'+Node.target);
				}
			});
			
			// retrieving nodes and relations
			graphDataObj.nodes = new vis.DataSet(ArrayNodes);
			graphDataObj.edges = new vis.DataSet(ArrayEdges);
			$("#networkVis").show();
		}
		// RETURNING GRAPH DATA FORMATTED
		return graphDataObj;
	}
	
	// LOAD GRAPH DATA FROM SERVER
	var loadNetwork = function(){
		logControl ? console.log('loadNetwork() -> ') : '';
		
		destroyNetwork();
		
	    // AJAX CALL - get GRAPH data
		$.ajax({ url:"/getgraph", type: 'GET', dataType: 'json', contentType: 'text/html',
			success: function(data) {

	        	links 		= data; 
				graphData	= getDataGraph();
				
				if( graphData.nodes.length > 0 ){
					containerNetwork = document.getElementById('networkVis');
					network = new vis.Network(containerNetwork, graphData, options);
					network.on("selectNode", function(params){
				
						if ( params.nodes.length == 1 ) {
							if ( network.isCluster( params.nodes[0] ) == true){
								network.openCluster( params.nodes[0] );
							}
							
							// Selected Node
							var selectedId = network.getSelection().nodes;
							var currentNode = graphData.nodes.get(selectedId);
							if( currentNode[0] != undefined ){
								drawGraphInfo(currentNode[0]);
							}
						}
					});
				}
			},
			error:function(data, status, er) { 
					$.alert({title: 'GRAPH ERROR!',content: 'ERror loading graph info on graph Controller.' });       
	        }
		}); 
		
		// ############## MOCKUP ###################### 
		/* links 		= graphJson.data		
		graphData	= getDataGraph();
		
		if( graphData.nodes.length > 0 ){
			containerNetwork = document.getElementById('networkVis');
			network = new vis.Network(containerNetwork, graphData, options);
			network.on("selectNode", function(params){
		
				if ( params.nodes.length == 1 ) {
					if ( network.isCluster( params.nodes[0] ) == true){
						network.openCluster( params.nodes[0] );
					}
					
					// Selected Node
					var selectedId = network.getSelection().nodes;
					var currentNode = graphData.nodes.get(selectedId);
					if( currentNode[0] != undefined ){
						drawGraphInfo(currentNode[0]);
					}
				}
			});
		} */
		// ############## MOCKUP ######################
		
		
		
		
	}
	
	// HANDLE GRAPH HEIGHT CONTAINER
	var handleGraphHeight = function(){
		logControl ? console.log('handleGraphHeight() -> ') : ''; 
		// Add Height
		$('#btn-addH').on('click',function(){
			$('#networkVis').height(function (index, height) { return (height + 100); });
		});		
		// Remove Height
		$('#btn-remH').on('click',function(){
			$('#networkVis').height(function (index, height) { return (height - 100); });
			if ( parseInt($('#networkVis').css('height')) <= 475 ){ $('#networkVis').css('height', options.height); }
						
		});
		// Restore Height
		$('#btn-resH').on('click',function(){
			$('#networkVis').css('height', options.height);					
		});		
	}
	
	
	// TOGGLE GRAPH INFO TABLE
	var toggleGraphInfoTable = function(){
		
		$('#btn-graphInfo').on('click', function(){ $('#TableInfoNetwork').fadeToggle() });
		
	}
	
	// CONTROLLER PUBLIC FUNCTIONS 
	return{
		
		// LOAD() JSON LOAD FROM TEMPLATE TO CONTROLLER
		load: function(Data) { 
			logControl ? console.log(LIB_TITLE + ': load()') : '';
			return graphReg = Data;
		},
		
		// INIT() CONTROLLER INIT CALLS
		init: function(){
			logControl ? console.log(LIB_TITLE + ': init()') : '';
			createGraphInfoTable();
			handleGraphHeight();
			handleHierarchical();
			handleCluster();
			toggleGraphInfoTable();
			loadNetwork();
		}		
	};
}();

// AUTO INIT CONTROLLER WHEN READY
jQuery(document).ready(function() {
	
	// LOADING JSON DATA FROM THE TEMPLATE (CONST, i18, ...)
	GraphController.load(graphJson);
	
	// AUTO INIT CONTROLLER.
	GraphController.init();
});
