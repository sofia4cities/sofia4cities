<?xml version="1.0" encoding="UTF-8"?>
<WebElementEntity>
   <description></description>
   <name>h3_Alarm</name>
   <tag></tag>
   <elementGuidId>bc127330-0886-4559-86b4-2bdc8428e7ae</elementGuidId>
   <selectorMethod>BASIC</selectorMethod>
   <useRalativeImagePath>false</useRalativeImagePath>
   <webElementProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>tag</name>
      <type>Main</type>
      <value>h3</value>
   </webElementProperties>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>class</name>
      <type>Main</type>
      <value>uppercase</value>
   </webElementProperties>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>onclick</name>
      <type>Main</type>
      <value>OntologyCreateController.schemaToTable(this,'datamodel_properties');</value>
   </webElementProperties>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>data-model</name>
      <type>Main</type>
      <value>5226a526-3c1b-4bba-bb09-97b369d33b3e</value>
   </webElementProperties>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>data-schema</name>
      <type>Main</type>
      <value>{
    &quot;$schema&quot;: &quot;http://json-schema.org/draft-04/schema#&quot;,
    &quot;title&quot;: &quot;PlantillaAlarm Schema&quot;,
    &quot;type&quot;: &quot;object&quot;,
    &quot;required&quot;: [
        &quot;Alarm&quot;
    ],
    &quot;properties&quot;: {
        &quot;Alarm&quot;: {
            &quot;type&quot;: &quot;string&quot;,
            &quot;$ref&quot;: &quot;#/datos&quot;
        }
    },
    &quot;datos&quot;: {
        &quot;description&quot;: &quot;Info Plantilla Alarm&quot;,
        &quot;type&quot;: &quot;object&quot;,
        &quot;required&quot;: [
            &quot;timestamp&quot;,
            &quot;assetId&quot;,
            &quot;severity&quot;
        ],
        &quot;properties&quot;: {
            &quot;timestamp&quot;: {
                &quot;type&quot;: &quot;object&quot;,
                &quot;required&quot;: [
                    &quot;$date&quot;
                ],
                &quot;properties&quot;: {
                    &quot;$date&quot;: {
                        &quot;type&quot;: &quot;string&quot;,
                        &quot;format&quot;: &quot;date-time&quot;
                    }
                },
                &quot;additionalProperties&quot;: false
            },
            &quot;assetId&quot;: {
                &quot;type&quot;: &quot;string&quot;
            },
            &quot;severity&quot;: {
                &quot;type&quot;: &quot;string&quot;,
                &quot;enum&quot;: [
                    &quot;LOW&quot;,
                    &quot;MEDIUM&quot;,
                    &quot;CRITICAL&quot;
                ]
            },
            &quot;alarmSource&quot;: {
                &quot;type&quot;: &quot;string&quot;
            },
            &quot;details&quot;: {
                &quot;type&quot;: &quot;string&quot;
            },
            &quot;status&quot;: {
                &quot;type&quot;: &quot;string&quot;
            }
        }
    }
}</value>
   </webElementProperties>
   <webElementProperties>
      <isSelected>true</isSelected>
      <matchCondition>equals</matchCondition>
      <name>text</name>
      <type>Main</type>
      <value>Alarm</value>
   </webElementProperties>
   <webElementProperties>
      <isSelected>false</isSelected>
      <matchCondition>equals</matchCondition>
      <name>xpath</name>
      <type>Main</type>
      <value>id(&quot;Alarm&quot;)/li[@class=&quot;mt-list-item datamodel-template&quot;]/div[@class=&quot;list-item-content&quot;]/h3[@class=&quot;uppercase&quot;]</value>
   </webElementProperties>
</WebElementEntity>
