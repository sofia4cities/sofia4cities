/**
 * Copyright Indra Sistemas, S.A.
 * 2013-2018 SPAIN
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.indra.sofia2.support.util.xml.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="runningDate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="runningNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="companyCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="productCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="tractionProvider" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="controlPoint" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="sequence" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="arrivalDelayCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="departureDelayCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="esbtimestamp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "runningDate",
    "runningNumber",
    "companyCode",
    "productCode",
    "tractionProvider",
    "controlPoint",
    "sequence",
    "arrivalDelayCode",
    "departureDelayCode",
    "timestamp",
    "esbtimestamp"
})
@XmlRootElement(name = "delayJustification")
public class DelayJustification {

    @XmlElement(required = true)
    protected String runningDate;
    @XmlElement(required = true)
    protected String runningNumber;
    @XmlElement(required = true)
    protected String companyCode;
    @XmlElement(required = true)
    protected String productCode;
    @XmlElement(required = true)
    protected String tractionProvider;
    @XmlElement(required = true)
    protected String controlPoint;
    @XmlElement(required = true)
    protected String sequence;
    @XmlElement(required = true)
    protected String arrivalDelayCode;
    @XmlElement(required = true)
    protected String departureDelayCode;
    @XmlElement(required = true)
    protected String timestamp;
    protected String esbtimestamp;

    /**
     * Gets the value of the runningDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRunningDate() {
        return runningDate;
    }

    /**
     * Sets the value of the runningDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRunningDate(String value) {
        this.runningDate = value;
    }

    /**
     * Gets the value of the runningNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRunningNumber() {
        return runningNumber;
    }

    /**
     * Sets the value of the runningNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRunningNumber(String value) {
        this.runningNumber = value;
    }

    /**
     * Gets the value of the companyCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCompanyCode() {
        return companyCode;
    }

    /**
     * Sets the value of the companyCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCompanyCode(String value) {
        this.companyCode = value;
    }

    /**
     * Gets the value of the productCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * Sets the value of the productCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductCode(String value) {
        this.productCode = value;
    }

    /**
     * Gets the value of the tractionProvider property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTractionProvider() {
        return tractionProvider;
    }

    /**
     * Sets the value of the tractionProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTractionProvider(String value) {
        this.tractionProvider = value;
    }

    /**
     * Gets the value of the controlPoint property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getControlPoint() {
        return controlPoint;
    }

    /**
     * Sets the value of the controlPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setControlPoint(String value) {
        this.controlPoint = value;
    }

    /**
     * Gets the value of the sequence property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSequence() {
        return sequence;
    }

    /**
     * Sets the value of the sequence property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSequence(String value) {
        this.sequence = value;
    }

    /**
     * Gets the value of the arrivalDelayCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArrivalDelayCode() {
        return arrivalDelayCode;
    }

    /**
     * Sets the value of the arrivalDelayCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArrivalDelayCode(String value) {
        this.arrivalDelayCode = value;
    }

    /**
     * Gets the value of the departureDelayCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDepartureDelayCode() {
        return departureDelayCode;
    }

    /**
     * Sets the value of the departureDelayCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDepartureDelayCode(String value) {
        this.departureDelayCode = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestamp(String value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the esbtimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEsbtimestamp() {
        return esbtimestamp;
    }

    /**
     * Sets the value of the esbtimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEsbtimestamp(String value) {
        this.esbtimestamp = value;
    }

}
