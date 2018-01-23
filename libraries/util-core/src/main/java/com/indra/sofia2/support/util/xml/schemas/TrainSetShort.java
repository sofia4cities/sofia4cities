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
 *         &lt;element name="trainSet">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="locomotive1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="locomotive2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="locomotive3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="totalVehicles" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="totalAxles" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="dangerousMaterial" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                   &lt;element name="securityOrders" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="timestamp" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="esbtimestamp" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "trainSet",
    "timestamp",
    "esbtimestamp"
})
@XmlRootElement(name = "trainSet_short")
public class TrainSetShort {

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
    protected TrainSetShort.TrainSet trainSet;
    @XmlElement(required = true)
    protected String timestamp;
    @XmlElement(required = true)
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
     * Gets the value of the trainSet property.
     * 
     * @return
     *     possible object is
     *     {@link TrainSetShort.TrainSet }
     *     
     */
    public TrainSetShort.TrainSet getTrainSet() {
        return trainSet;
    }

    /**
     * Sets the value of the trainSet property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrainSetShort.TrainSet }
     *     
     */
    public void setTrainSet(TrainSetShort.TrainSet value) {
        this.trainSet = value;
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
     *         &lt;element name="locomotive1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="locomotive2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="locomotive3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="totalVehicles" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="totalAxles" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="weight" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="dangerousMaterial" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *         &lt;element name="securityOrders" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "locomotive1",
        "locomotive2",
        "locomotive3",
        "totalVehicles",
        "totalAxles",
        "length",
        "weight",
        "dangerousMaterial",
        "securityOrders"
    })
    public static class TrainSet {

        protected String locomotive1;
        protected String locomotive2;
        protected String locomotive3;
        @XmlElement(required = true)
        protected String totalVehicles;
        @XmlElement(required = true)
        protected String totalAxles;
        @XmlElement(required = true)
        protected String length;
        @XmlElement(required = true)
        protected String weight;
        @XmlElement(required = true)
        protected String dangerousMaterial;
        @XmlElement(required = true)
        protected String securityOrders;

        /**
         * Gets the value of the locomotive1 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLocomotive1() {
            return locomotive1;
        }

        /**
         * Sets the value of the locomotive1 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLocomotive1(String value) {
            this.locomotive1 = value;
        }

        /**
         * Gets the value of the locomotive2 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLocomotive2() {
            return locomotive2;
        }

        /**
         * Sets the value of the locomotive2 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLocomotive2(String value) {
            this.locomotive2 = value;
        }

        /**
         * Gets the value of the locomotive3 property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLocomotive3() {
            return locomotive3;
        }

        /**
         * Sets the value of the locomotive3 property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLocomotive3(String value) {
            this.locomotive3 = value;
        }

        /**
         * Gets the value of the totalVehicles property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotalVehicles() {
            return totalVehicles;
        }

        /**
         * Sets the value of the totalVehicles property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotalVehicles(String value) {
            this.totalVehicles = value;
        }

        /**
         * Gets the value of the totalAxles property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getTotalAxles() {
            return totalAxles;
        }

        /**
         * Sets the value of the totalAxles property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setTotalAxles(String value) {
            this.totalAxles = value;
        }

        /**
         * Gets the value of the length property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLength() {
            return length;
        }

        /**
         * Sets the value of the length property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLength(String value) {
            this.length = value;
        }

        /**
         * Gets the value of the weight property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getWeight() {
            return weight;
        }

        /**
         * Sets the value of the weight property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setWeight(String value) {
            this.weight = value;
        }

        /**
         * Gets the value of the dangerousMaterial property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDangerousMaterial() {
            return dangerousMaterial;
        }

        /**
         * Sets the value of the dangerousMaterial property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDangerousMaterial(String value) {
            this.dangerousMaterial = value;
        }

        /**
         * Gets the value of the securityOrders property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSecurityOrders() {
            return securityOrders;
        }

        /**
         * Sets the value of the securityOrders property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSecurityOrders(String value) {
            this.securityOrders = value;
        }

    }

}
