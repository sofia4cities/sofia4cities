import static com.kms.katalon.core.checkpoint.CheckpointFactory.findCheckpoint
import static com.kms.katalon.core.testcase.TestCaseFactory.findTestCase
import static com.kms.katalon.core.testdata.TestDataFactory.findTestData
import static com.kms.katalon.core.testobject.ObjectRepository.findTestObject
import com.kms.katalon.core.checkpoint.Checkpoint as Checkpoint
import com.kms.katalon.core.checkpoint.CheckpointFactory as CheckpointFactory
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as MobileBuiltInKeywords
import com.kms.katalon.core.mobile.keyword.MobileBuiltInKeywords as Mobile
import com.kms.katalon.core.model.FailureHandling as FailureHandling
import com.kms.katalon.core.testcase.TestCase as TestCase
import com.kms.katalon.core.testcase.TestCaseFactory as TestCaseFactory
import com.kms.katalon.core.testdata.TestData as TestData
import com.kms.katalon.core.testdata.TestDataFactory as TestDataFactory
import com.kms.katalon.core.testobject.ObjectRepository as ObjectRepository
import com.kms.katalon.core.testobject.TestObject as TestObject
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WSBuiltInKeywords
import com.kms.katalon.core.webservice.keyword.WSBuiltInKeywords as WS
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUiBuiltInKeywords
import com.kms.katalon.core.webui.keyword.WebUiBuiltInKeywords as WebUI
import internal.GlobalVariable as GlobalVariable
import org.openqa.selenium.Keys as Keys

WebUI.openBrowser('')

WebUI.navigateToUrl('http://sofia2-benchmarks.westeurope.cloudapp.azure.com/controlpanel/login')

WebUI.waitForElementVisible(findTestObject('Object Repository/Page_Control Panel Login/input_username'), 10)

WebUI.setText(findTestObject('Object Repository/Page_Control Panel Login/input_username'), 'administrator')

WebUI.setText(findTestObject('Page_Control Panel Login/input_password'), 'changeIt!')

WebUI.click(findTestObject('Page_Control Panel Login/input_full-width'))

WebUI.waitForElementVisible(findTestObject('Object Repository/Page_Sofia4Cities Control Panel/i_flaticon-network'), 10)

WebUI.mouseOver(findTestObject('Object Repository/Page_Sofia4Cities Control Panel/i_flaticon-network'))

WebUI.waitForElementVisible(findTestObject('Page_Sofia4Cities Control Panel/span_ModelosOntologas'), 10)

WebUI.click(findTestObject('Page_Sofia4Cities Control Panel/span_ModelosOntologas'))

WebUI.click(findTestObject('Page_Sofia4Cities Control Panel/span_Nuevo'))

WebUI.click(findTestObject('Page_Sofia4Cities Control Panel/a_Asistente de Ontologas'))

WebUI.doubleClick(findTestObject('Page_Sofia4Cities Control Panel/input_identification'))

WebUI.waitForElementVisible(findTestObject('Page_Sofia4Cities Control Panel/input_identification'), 10)
WebUI.setText(findTestObject('Page_Sofia4Cities Control Panel/input_identification'),GlobalVariable.ONTOLOGY)

findTestObject('Object Repository/Page_Sofia4Cities Control Panel/input')
WebUI.click(findTestObject('Object Repository/Page_Sofia4Cities Control Panel/input'))
WebUI.setText(findTestObject('Object Repository/Page_Sofia4Cities Control Panel/input'), 'meta')
WebUI.focus(findTestObject('Page_Sofia4Cities Control Panel/textarea_description'))

WebUI.setText(findTestObject('Page_Sofia4Cities Control Panel/textarea_description'), 'Description')


WebUI.click(findTestObject('Page_Sofia4Cities Control Panel/span_General'))

WebUI.doubleClick(findTestObject('Page_Sofia4Cities Control Panel/p_Base Alarm assetId timestamp'))

WebUI.doubleClick(findTestObject('Page_Sofia4Cities Control Panel/h3_Alarm'))

WebUI.click(findTestObject('Page_Sofia4Cities Control Panel/button_Update Schema'))

//WebUI.click(findTestObject('Page_Sofia4Cities Control Panel/html_Sofia4Cities Control Pane_2'))

WebUI.click(findTestObject('Page_Sofia4Cities Control Panel/span_Generar Instancia'))

WebUI.click(findTestObject('Page_Sofia4Cities Control Panel/button_Nuevo'))

//WebUI.closeBrowser()

