package com.indracompany.sofia2.controlpanel.controller.welcome;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.annotation.JsonValue;


import lombok.*;

public class GraphDTO {




	@Getter @Setter private String source;
	@Getter @Setter private String target;

	@Getter @Setter private String title;
	@Getter @Setter private String linkCreate;
	@Getter @Setter private String linkSource;
	@Getter @Setter private String linkTarget;

	@Getter @Setter private String classSource;
	@Getter @Setter private String classTarget;

	@Getter @Setter private String nameSource;
	@Getter @Setter private String nameTarget;

	@Getter @Setter private String type;
	
	public GraphDTO(String source, String target, String linkSource, String linkTarget, String classSource,
			String classTarget, String nameSource, String nameTarget, String type) {
		this.source = source;
		this.target = target;
		this.linkSource = linkSource;
		this.linkTarget = linkTarget;
		this.classSource = classSource;
		this.classTarget = classTarget;
		this.nameSource = nameSource;
		this.nameTarget = nameTarget;
		this.type = type;
	}
	
	public GraphDTO(String source, String target, String linkSource, String linkTarget, String classSource,
			String classTarget,String nameSource, String nameTarget, String type,String title, String linkCreate) {
		super();
		this.source = source;
		this.target = target;
		this.linkSource = linkSource;
		this.linkTarget = linkTarget;
		this.classSource = classSource;
		this.classTarget = classTarget;
		this.title = title;
		this.linkCreate = linkCreate;
		this.nameSource = nameSource;
		this.nameTarget = nameTarget;
		this.type = type;
	}
	

	public static GraphDTO constructSingleNode(String source,String linkSource,String classSource,String nameSource){
		return new GraphDTO(source, source, linkSource, linkSource, classSource, classSource, nameSource, nameSource, null);
	}
	
	public static GraphDTO constructSingleNodeWithTitleAndCreateLink(String source,String linkSource,String classSource,String nameSource,String title,String linkCreate){
		return new GraphDTO(source, source, linkSource, linkSource, classSource, classSource, nameSource, nameSource, null,title,linkCreate);
	}
	
	
	@Override
	@JsonRawValue
	public String toString()
	{
		return super.toString();
		
	}
}
