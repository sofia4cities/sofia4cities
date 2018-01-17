var Grafo = function(){
	var logControl = 1;
	var alertContainer = "#networkAlert";
	var nodes = [];
	var data = [];
	var edges = [];
	var ArraySource = [];
	var jsonProjects = {};
	var options = {};
	var jsonIndex = {};
	var grafoData = {nodes: [],edges: []};
	var containerNetwork = new Object();
	var network = null;
	var LANG_JSON_HEADER = {
		'TAGALERTA':'Alert',
		'TAGPROPIEDADES':'Properties',
		'TAGVALOR':'Value',
		'BTNCOLUMNAS':'Columnas',
		'TAGPROPIEDADES':'Properties'
	};
	var LANG_JSON = {
		'js-GC-name':'Nombre'
	};
	options = {	
		manipulation: false,
		interaction: {
			dragNodes: true,
			dragView: true,
			hover:true
		},
		/*physics: {
			barnesHut: {
			      gravitationalConstant: -6450,
			      centralGravity: 0,
			      springLength: 70,
			      springConstant: 0.09,
			      avoidOverlap: 1
			    },
			    minVelocity: 1
		},
		physics: {
		    forceAtlas2Based: {
		      gravitationalConstant: -70,
		      springLength: 85,
		      damping: 1,
		      centralGravity: 0.035,
		      springConstant: 0.3
		    },
		    maxVelocity: 3,
		    minVelocity: 0.75,
		    timestep: 1,
		    solver: "forceAtlas2Based"
		  },*/
		physics: {
		    forceAtlas2Based: {
		      gravitationalConstant: -70,

		    },
		    solver: "forceAtlas2Based"
		  },
		nodes: {
		  shadow: false,		  
          borderWidth:4,
          size:30,
		  scaling:{
            label: {
              min:10,
              max:20
            }
          },
	      color: {
            border: '#222222',
            background: '#666666'
          },
          font:{color:'#666'}
        },
        edges: {
			arrows: {
				to: {
					enabled: true,
					scaleFactor: 1
				}
			},
			font: {
				align: 'middle',
				size: 10
			},
			shadow: false,
			smooth: true,
			labelHighlightBold: true,
			color: '#b5afaf'
		},

		autoResize : true,		
		height : '500px',
		groups:{
			resolved :{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf2d2',
					size: 30,
					color: '#ed6b75'
				}
			},
			licensing:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf2d0',
					size: 30,
					color: '#c49f47'
				}
			},
			usuario:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf2be',
					size: 60,
					color: '#4B77BE'
				}
			},
			gadgets:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf1e6',
					size: 40,
					color: '#525e64'
				}
			},
			visualizacion:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf080',
					size: 50,
					color: '#525e64'
				}
			},
			analytics:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf0c3',
					size: 50,
					color: '#525e64'
				}
			},
			kps:{
				shape: 'icon',
				icon: {
					face: 'FontAwesome',
					code: '\uf2db',
					size: 40,
					color: '#5e738b'
				}
			},
			ontologias:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf0e8',
					size: 40,
					color: '#5e738b'
				}
			},
			notebooks:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf02d',
					size: 40,
					color: '#5e738b'
				}
			},
			dashboards:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf0e4',
					size: 40,
					color: '#5e738b'
				}
			},
			scripts:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf0b0',
					size: 40,
					color: '#5e738b'
				}
			},
			proyectos:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf07b',
					size: 40,
					color: '#5e738b'
				}
			},
			pipelines:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf15b',
					size: 40,
					color: '#5e738b'
				}
			},
			project:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf114',
					size: 30,
					color: '#c49f47'
				}
			},
			gadget:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf1fe',
					size: 30,
					color: '#c49f47'
				}
			},
			script:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf1c9',
					size: 30,
					color: '#5e738b'
				}
			},
			pipeline:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf2d0',
					size: 30,
					color: '#c49f47'
				}
			},
			dashboard:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf1fe',
					size: 30,
					color: '#c49f47'
				}
			},
			ontology:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf1e0',
					size: 30,
					color: '#c49f47'
				}
			},
			kp:{
				shape: 'icon',
				icon: {
					face: 'FontAwesome',
					code: '\uf10b',
					size: 35,
					color: '#c49f47'
				}
			},
			notebook:{
				shape: 'icon',
				icon: {
					face: 'FontAwesome',
					code: '\uf0f2',
					size: 30,
					color: '#c49f47'
				}
			},
			Clusterproyectos:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf07b',
					size: 60,
					color: '#5e738b'
				}
			},
			Clusterdashboards:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf0e4',
					size: 60,
					color: '#5e738b'
				}
			},
			
			Clusterontologias:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf0e8',
					size: 60,
					color: '#5e738b'
				}
			},
			Clusternotebooks:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf02d',
					size: 60,
					color: '#5e738b'
				}
			},
			Clusterpipelines:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf15b',
					size: 60,
					color: '#5e738b'
				}
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
				icon:{
					face: 'FontAwesome',
					code: '\uf0b0',
					size: 60,
					color: '#5e738b'
				}
			},
			Clustergadgets:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf1e6',
					size: 60,
					color: '#525e64'
				}
			},
			Clusterlicensing:{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf2d0',
					size: 60,
					color: '#c49f47'
				}
			},
			Clusterresolved :{
				shape: 'icon',
				icon:{
					face: 'FontAwesome',
					code: '\uf2d2',
					size: 60,
					color: '#ed6b75'
				}
			}
		}
	};
	
	var clusterByConnection= function(){
		network.setData(grafoData);
		network.clusterByConnection(1)
	}
	var clusterGrafo= function(obj){
		/*logControl ? console.log('clusterGrafo') : '';*/
		var action = $(obj).attr("data-action");
		logControl ? console.log('action:'+action) : '';
		console.log(grafoData);
		if(grafoData.nodes.length > 0){
			if(action == "source"){
				clusterGrafoBySource();
			}else if(action == "type"){
				clusterGrafoByType();
			}
		}
	}
	function clusterGrafoBySource(){
		//logControl ? console.log('clusterGrafoBySource') : '';
		//logControl ? console.log('ArraySource') : '';
		//logControl ? console.log(ArraySource) : '';
		network.setData(grafoData);
		var clusterOptionsByData;
		var target = '';
		//logControl ? console.log(options.groups) : '';
		for(var i = 0; i < ArraySource.length; i++){
			var idSource = ArraySource[i].id;
			var target = ArraySource[i].target;
			var group = ArraySource[i].group;
			clusterOptionsByData = {
				joinCondition: function (childOptions) {
					return childOptions.category == target;
				},
				processProperties: function (clusterOptions, childNodes, childEdges) {
					var totalMass = 0;
					for (var i = 0; i < childNodes.length; i++) {
						totalMass += childNodes[i].mass;
					}
					clusterOptions.mass = totalMass;
					return clusterOptions;
				},
				clusterNodeProperties: {id:'cluster:'+idSource,borderWidth:3,group:'Cluster'+group,'label':target}
			};
			network.cluster(clusterOptionsByData);
		}
		//logControl ? console.log(options.groups) : '';
	};
	function clusterGrafoByType(){
		//logControl ? console.log('clusterGrafoByStatus') : '';
		var ArrayType = ['licensing','resolved'];
		network.setData(grafoData);
		var clusterOptionsByData;
		for(var i = 0; i < ArrayType.length; i++){
			var type = ArrayType[i];
			clusterOptionsByData = {
				joinCondition: function (childOptions) {
					return childOptions.type == type;
				},
				processProperties: function (clusterOptions, childNodes, childEdges) {
					var totalMass = 0;
					for (var i = 0; i < childNodes.length; i++) {
						totalMass += childNodes[i].mass;
					}
					clusterOptions.mass = totalMass;
					return clusterOptions;
				},
				clusterNodeProperties: {id:'cluster:'+type,borderWidth:3,group:'Cluster'+type,label:type}
			};
			network.cluster(clusterOptionsByData);
		}
	};
	function clusterGrafoByProject(){
		//logControl ? console.log('clusterGrafoByProject') : '';
		//logControl ? console.log('jsonProjects:'+jsonProjects) : '';
		network.setData(grafoData);
		var clusterOptionsByData;
		$.each(jsonProjects,function(project,value){
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
				clusterNodeProperties: {id:'cluster:'+project,borderWidth:3,group:'projectCluster',label:project}
			};
			network.cluster(clusterOptionsByData);
		});
	};
	function destroyNetwork(){
		if(network !== null){
			network.destroy();
			network = null;
		}
	}
	function hierarchicalNetwork(obj){
		var direction = $(obj).attr("data-action");
		//logControl ? console.log('direction:'+direction) : '';
		if(direction != ""){
			destroyNetwork();
			if(direction != "default"){
				options.layout = {};
				options.layout.hierarchical = {};
				options.layout.hierarchical.direction = direction;
				options.layout.hierarchical.sortMethod = 'directed';
			}else{
				delete options.layout;
			}
			containerNetwork = document.getElementById('networkVis');
			network = new vis.Network(containerNetwork, grafoData, options);
			network.on("selectNode", function(params){
				//logControl ? console.log('nodes.length:'+params.nodes.length) : '';
				debugger;
				if(params.nodes.length == 1){
					if(network.isCluster(params.nodes[0]) == true){
						network.openCluster(params.nodes[0]);
					}
					
					//Nodo Seleccionado
					var selectedId = network.getSelection().nodes;
					var currentNode = grafoData.nodes.get(selectedId);
					/*logControl ? console.log('currentNode') : '';*/
					/*logControl ? console.log(currentNode) : '';*/
					/*logControl ? console.log("type:"+currentNode[0].type) : '';*/
					if(currentNode[0] != undefined){
						drawGrafoInfo(currentNode[0]);
					}
				}
			});
		}
	}
	
	var links=[];
	var getDataGrafo  = function(){
		/*logControl ? console.log('getDataGrafo') : '';*/
		var Return = {nodes:[],edges:[]};
		var ArrayNodes = [];
		var ArrayEdges = [];
		var idRef = {};
		var target = '';
		var nodes={};
		var avoidDuplicates=[];
		
		if ($("#id_panel_botones")){
			$("#id_panel_botones").hide();
		}

		$("#networkVis").hide();
		
		
		/*logControl ? console.log('links') : '';*/
		/*logControl ? console.log(links) : '';*/
		if (!$.isEmptyObject(links)){
			/*Se crean los nodos con la informacion*/
			if ($("#id_panel_botones")){
				$("#id_panel_botones").show();
			}
			$.each(links,function(Index,Node){
				if($.inArray(Node.nameTarget,avoidDuplicates)===-1){
					if(Node.type === undefined){Node.type="";};
					idRef[Node.target] = Index;
					var dataJson= {'id':Index,'label':Node.nameTarget,'source':Node.source,'type':Node.type,'linkTarget':Node.linkTarget,'nameSource':Node.nameSource,'group':Node.classTarget,'title':Node.title,'linkCreate': Node.linkCreate};

					group = dataJson.group.toLowerCase();
					type = dataJson.type.toLowerCase();
					if(options.groups[group] !== undefined){
						dataJson.group = group;
					}else if(options.groups[type] !== undefined){
						dataJson.group = type;
					}else{
						dataJson.group = 'licensing';
					}
					dataJson.category = Node.source;
					if(Node.type == 'suit'){
						dataJson.category = Node.target;
						ArraySource.push({'id':Index,'target':Node.target,'group':dataJson.group});
					}
				
					ArrayNodes.push(dataJson);
					avoidDuplicates.push(Node.nameTarget);
				}
				
			});
			/*Se crean las relaciones entre nodos*/
			$.each(links,function(Index,Node){
				if( (idRef[Node.source] !== undefined)&&(idRef[Node.target] !== undefined)&&(idRef[Node.source]!==idRef[Node.target]) ){
					ArrayEdges.push({from:idRef[Node.source],to:idRef[Node.target]});
				}else{
					//console.log("Verificar nodo:"+Index+' - Source:'+Node.source+' Target:'+Node.target);
				}
			});
			Return.nodes = new vis.DataSet(ArrayNodes);
			Return.edges = new vis.DataSet(ArrayEdges);
			$("#networkVis").show();
		};
		/*logControl ? console.log(Return) : '';*/
		return Return;
	}
	
	
	var loadNetworkTab = function(){
		destroyNetwork();
	      $.ajax({
	          url:"getgraph",
	          type: 'GET',
	          dataType: 'json', 
	          contentType: 'text/html',  
	          success: function(data) {

	        	  
					links=data;
	        	    grafoData = getDataGrafo();
					/*logControl ? console.log('grafoDatagrafoData') : '';*/
					/*logControl ? console.log(grafoData) : '';*/
					if(grafoData.nodes.length > 0){
						containerNetwork = document.getElementById('networkVis');
						network = new vis.Network(containerNetwork, grafoData, options);
						network.on("selectNode", function(params){
							//logControl ? console.log('nodes.length:'+params.nodes.length) : '';
							if (params.nodes.length == 1) {
								if (network.isCluster(params.nodes[0]) == true){
									network.openCluster(params.nodes[0]);
								}
								//Nodo Seleccionado
								var selectedId = network.getSelection().nodes;
								var currentNode = grafoData.nodes.get(selectedId);
								if(currentNode[0] != undefined){
										drawGrafoInfo(currentNode[0]);
								}
							}
						});
					}
	          },
	          error:function(data, status, er) { 
					console.log(data);       
	         }
	  });

	}
	
	function limpiarTabla(){
		$("#id_nombre").html();
		$("#id_source").html();
		if ($("#id_enlaceS")){
			$("#id_enlaceS").removeAttr("href");
		}
		
		if ($("#id_enlaceC")){
			$("#id_enlaceC").removeAttr("href");
		}
		$("#id_tr_enlaceS").hide();
		$("#id_tr_enlaceC").hide();
		
	}
	var drawGrafoInfo = function(currentNode){
		/*logControl ? console.log('drawGrafoInfo') : '';*/
		/*Se procesan los valores null por vacios*/
		$.each(currentNode,function(key,value){
			if(value == null ){
				currentNode[key] = "";
			}
		});
		//debugger;
		limpiarTabla();
		$("#TableInfoNetwork").show();
		//Rellena la tabla con los datos del nodo
		$("#id_nombre").html(currentNode.label);
		$("#id_source").html(currentNode.nameSource);
		
		if (currentNode.linkTarget){
			$("#id_tr_enlaceS").show();
			$("#id_enlaceS").attr("href",currentNode.linkTarget);
		}
		if (currentNode.linkCreate){
			$("#id_tr_enlaceC").show();
			$("#id_enlaceC").attr("href",currentNode.linkCreate);
		}
		

	}
	var accesGO = function(obj){
		var url = $(obj).attr('data-href');
		if(url != ""){
			location.href = url;
		}
	}
	// FUNCION PARA CAMBIAR EL COLUMNADO DE LOS ELEMENTOS EN LA PESTAÑA MONITORIZACION
	var changeColumns = function(pBtn){
		$(pBtn).addClass('active');
		$(pBtn).siblings("button[data-action='changeColumns']").removeClass('active');
		var colGro = $(pBtn).attr('data-col-group');
		var colNum = $(pBtn).attr('data-col-number');
		if ((colNum != 1) && (colNum != 2)) { colNum = 2;}
		// IDENTIFICACION DEL TIPO DE COLUMNADO
		var typCol = "md";
		var winWid = window.innerWidth;
		if (winWid < 768) { typCol = "xs";}
		else if (winWid >= 768 && winWid < 992) { typCol = "sm";}
		else if (winWid >= 992 && winWid < 1200) { typCol = "md";}
		else if (winWid >= 1200) { typCol = "lg";}
		if ((typCol != "md") && (typCol != "lg")) { colNum = 1;}
		var colVal = parseInt(12 / colNum);
		$(pBtn).closest("div.portlet").find("div[data-col-group='"+colGro+"']").each(function(){
			var divCls = $(this).attr('class');
			var divHid = $(this).attr('data-col-hide');
			var divIni = $(this).attr('data-col-init');
			var arrCls = divCls.split(' ');
			$.each(arrCls,function(aIdx,aVal){
				if (aVal.indexOf('col-md-') != -1){
					arrCls[aIdx] = "col-md-"+colVal;
					if ((colNum != 1) && (divIni != "") && (divIni != undefined) && (divIni != null)) { arrCls[aIdx] = "col-md-"+divIni;}
				}
				if (aVal.indexOf('col-lg-') != -1){
					arrCls[aIdx] = "col-lg-"+colVal;
					if ((colNum != 1) && (divIni != "") && (divIni != undefined) && (divIni != null)) { arrCls[aIdx] = "col-lg-"+divIni;}
				}
			});
			var strCls = arrCls.join(" ");
			$(this).attr('class',strCls);
			var oEmpty = $(this).find("div.portlet-sortable-empty");
			oEmpty.css('height',"5px");
			$(this).removeClass('hide');
			if (colNum == 1){
				oEmpty.css('height',"0px");
				if (divHid == "true") { $(this).addClass('hide');}
			}
		});
	}
	var initNetwork = function(){
		logControl ? console.log('initNetwork') : '';
		/*Se adiciona los eventos para la carga de las pestaña*/
		$("a[data-toggle='tab'][href='#NETWORK']").on('shown.bs.tab',function(e){
			loadNetworkTab();
		});
		$("a[data-toggle='tab'][href='#NETWORK']").on('hidden.bs.modal', function (e) {
			/*Al salir de la pestaña*/
		})
		// CONFIGURACION DE BOTONES PARA CAMBIO DE COLUMNADO
		$("button[data-action='changeColumns']").each(function(){
			$(this).tooltip({
				title: LANG_JSON["js-GC-view"]+$(this).attr("data-col-number")+' ' + LANG_JSON_HEADER["BTNCOLUMNAS"],
				container: 'body',
				placement: 'bottom',
			});
			$(this).on('click',function() { changeColumns(this);});
		});
		$("#hierarchicalOptions > li > a").each(function(){
			$(this).on('click',function(){
				hierarchicalNetwork(this);
			});
		});
		$("#clusterOptions > li > a").each(function(){
			$(this).on('click',function(){
				clusterGrafo(this);
			});
		});
	}
	return{
		load: function(){
			loadNetworkTab();
		},
		init: function(){
			initNetwork();
		}
	}
}()