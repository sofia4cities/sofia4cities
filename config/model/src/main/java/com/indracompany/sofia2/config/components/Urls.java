package com.indracompany.sofia2.config.components;


import lombok.Data;

@Data
public class Urls {

	public Iotbroker iotbroker;

	public ScriptingEngine scriptingEngine;

	public FlowEngine flowEngine;

	public RouterStandAlone routerStandAlone;

	public ApiManager apiManager;

}