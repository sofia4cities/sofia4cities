# Getting Started Guide

## Installing the Development Environment

[See documentation](../how-to-execute-devenv/README.md)

## Downloading the Source Code

[See documentation](../how-to-execute-devenv/README.md)

## Compiling the Project

[See documentation](../how-to-execute-devenv/README.md)

## Integration with Eclipse IDE

[See documentation](../how-to-execute-devenv/README.md)

## Running the Platform

[See documentation](../how-to-execute-devenv/README.md)

## Basic Usage of the Platform

After performing the previous steps, you are ready to start using the platform.

The main access to use the platform is
[https://rancher.sofia4cities.com/controlpanel](https://rancher.sofia4cities.com/controlpanel). 
This will load the login form to access to the platform.

Within the installation, some users are created by default. 
You are free to change them or to create more users.

Please, use **administrator** login and **changeIt!** password to go into the platform with administrator role.

### Creating an Ontology

The first step it to model the data that we want to use.
To do that, go to the *DATA MODELLING -> Models / Ontologies* menu option ![Ontology List](images/Ontology_list.png).

This option shows the available ontologies.
Use the **Create** option at the right upper corner to create a new *Ontology*.

An *Ontology* represents the data model of your data.

Complete the following information for your new *Ontology* ![Ontology creation](images/Ontology_creation.png):

- Name: provide a unique name of the *Ontology*.
- Meta-Information: insert keywords to classify *Ontologies*. Use commas to introduce several keywords.
- Active: indicate if the *Ontology* is active. It is not possible to use inactive *Ontologies*.
- Public: indicate if the *Ontology* is public. All users will be able to query the data stored in the public *Ontologies*.
- Description: provide a description that helps identify the goal of the *Ontology*.
- Ontology Template: select one of the available templates to create the ontology. The most basic template is **General -> EmptyBase**. After selecting the template, add all the properties that you need in the ontology using the **ADD NEW PROPERTY** button. When you finish of adding properties use the **UPDATE SCHEMA** to add all the new properties to the *Ontology*. Finally, you can save the *Ontology* using the **New** Button at the end of the page.

### Creating a Device

After defining the ontology it is possible to define *Devices* that work with the ontology.
A *Device* is the representation of a physical device into the platform.
These *Devices* will generate or consume data.
To do that, go to the *DEVICES -> Definition of Devices* menu option. ![Device List](images/Device_list.png)
Use the **Create** button placed in the right upper corner to define a new *Device*.

Complete the following information for the new *Device*: ![Device Creation](images/Device_creation.png)

- Identification: provide a unique name for the *Device*.
- Description: add a description for the *Device*.
- Ontologies: it is possible to configure access to several ontologies.
  - Ontology: ontology identification.
  - Access Level: the type of access configured.
- Meta Information: Additional information about the device. The information will be added as key values pairs.
  - name: the name of the key.
  - value: the value of the property.

### Creating a Simulator

It is possible de define simulators for the device to help test the ontology and to generate data to aid in development.
To do that, go to the *DEVICES -> Devices Simulator* menu option and use the **Create** button placed in the right upper corner to define a new *Device Simulator*. ![Simulator Creation](images/Simulator_list.png)

Complete the following information for the new *Device Simulator*: ![Simulator Creation](images/Simulator_creation.png)

- Identification: Choose a unique name for the *Device Simulator*.
- Device: choose one of the available *Devices*.
- Token: choose one of the tokens defined in the *Device*.
- Ontology: choose one of the ontologies in which the device has insert authorization.
- Time between inserts: indicate the time in seconds between each insert in the ontology.
- Ontology Properties: fulfil the information required to generate values for each field of the ontology.

Finally, it is necessary to save the simulator by using the **New** button.

The *Control Panel* web application register the simulation task. For executing the simulation it is necessary to start the application device-simulator.

To do that, go to S:\sources\sofia2-s4c\modules\device-simulator and execute:

```sh
mvn spring-boot:run
```

You can see that the simulator insert data using the **TOOLS -> Database Query Tools**.

### Creating a Dashboard

### Creating an API REST

It is possible to publish operations over your *Ontologies* using creating APIs REST.

To use the created APIs, it is necessary to start up a new service called *API Manager*.
Execute S:\start.bat, go to S:\sources\sofia2-s4c\modules\api-manager and run:

```sh
mvn spring-boot:run
```

After the *API Manager* server starts go back to the *Control Panel* web application and use the *API MANAGER -> APIs Definition* menu option. ![Apimanager list](images/Apimanager_list.png) 
Once this is done, use the **Create** button.

You have to provide several information to create the API: ![Apimanager creation](images/Apimanager_creation.png)

- Identification: select a unique name for your API.
- API type: currently only expose an *Ontology* as API REST is available.
- Ontology: choose the *Ontology* used in the API.
- Description: provide a description for the API.
- Category: choose the category that better match your API.
- Public: if marked, this API will be visible for all the users and all the user will be able to use it.
- Meta-inf: provide the meta information that you want for your API.
- Image: if you want to use an image for the representation of your API.
- Operations: then you have to choose the operations that you want to expose in your API. There are several pre created operations. Additionally, you can provide any other operation based on queries.
  - QUERY(ID): if selected, it allows to use get data by id operation from the *Ontology*.
  - INSERT: if selected, it allows to perform insert operations to the *Ontology*.
  - UPDATE: if selected, it allows to perform update operations to the *Ontology*.
  - DELETE(ID): if selected, it allows to perform delete operations by the id of the data stored in the *Ontology*.
  - QUERY CUSTOM: this option allows to define all the required operations that could be required based on database queries. For example, the next query with SQLLIKE syntax will return all the data from an *Ontology* named *<ontology_name>*: 

  ```SQL
  select * from <ontology_name>
  ```

  Finally, use the **Edit** button to save the API data.

  The next step is to create an *API Manager Token*. These tokens are used to authorize the REST operations. ![Tokens list](images/Apimanager_token_list.png)

  To test the API you can use the integrated *swagger* client.
  Go to the *API MANAGER -> APIs Definition* menu option, and use the **SWAGGER** option of the API that you want to test.
  This opens a form with all the operations of the API.
  You have to provide valid parameters for the operations and in the **X-SOFIA2-APIKey** you have to provide a valid *API Token* generated as it is explained above.

  The same test can be done with an external REST client, for instance Postman.
  This is done by including in the header of the HTTP requests one parameter with key **X-SOFIA2-APIKey** and the token as value.