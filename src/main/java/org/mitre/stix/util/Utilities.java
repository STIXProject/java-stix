package org.mitre.stix.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

/**
 * A collection of utility helper methods.
 * 
 * @author nemonik (Michael Joseph Walsh <github.com@nemonik.com>)
 */
public class Utilities {

	private static final String XML_SCHEMA_INSTANCE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String XML_NAMESPACE = "http://www.w3.org/2000/xmlns/";

	@SuppressWarnings("serial")
	public static final Map<String, String> DEFAULT_XML_SCHEMA_LOCATIONS = Collections
			.unmodifiableMap(new HashMap<String, String>() {
				{
					// Schema locations for namespaces defined by STIX schemas
					put("http://data-marking.mitre.org/Marking-1",
							"http://stix.mitre.org/XMLSchema/data_marking/1.1.1/data_marking.xsd");
					put("http://data-marking.mitre.org/extensions/MarkingStructure#Simple-1",
							"http://stix.mitre.org/XMLSchema/extensions/marking/simple/1.1.1/simple_marking.xsd");
					put("http://data-marking.mitre.org/extensions/MarkingStructure#TLP-1",
							"http://stix.mitre.org/XMLSchema/extensions/marking/tlp/1.1.1/tlp_marking.xsd");
					put("http://data-marking.mitre.org/extensions/MarkingStructure#Terms_Of_Use-1",
							"http://stix.mitre.org/XMLSchema/extensions/marking/terms_of_use/1.0.1/terms_of_use_marking.xsd");
					put("http://stix.mitre.org/Campaign-1",
							"http://stix.mitre.org/XMLSchema/campaign/1.1.1/campaign.xsd");
					put("http://stix.mitre.org/CourseOfAction-1",
							"http://stix.mitre.org/XMLSchema/course_of_action/1.1.1/course_of_action.xsd");
					put("http://stix.mitre.org/ExploitTarget-1",
							"http://stix.mitre.org/XMLSchema/exploit_target/1.1.1/exploit_target.xsd");
					put("http://stix.mitre.org/Incident-1",
							"http://stix.mitre.org/XMLSchema/incident/1.1.1/incident.xsd");
					put("http://stix.mitre.org/Indicator-2",
							"http://stix.mitre.org/XMLSchema/indicator/2.1.1/indicator.xsd");
					put("http://stix.mitre.org/TTP-1",
							"http://stix.mitre.org/XMLSchema/ttp/1.1.1/ttp.xsd");
					put("http://stix.mitre.org/ThreatActor-1",
							"http://stix.mitre.org/XMLSchema/threat_actor/1.1.1/threat_actor.xsd");
					put("http://stix.mitre.org/common-1",
							"http://stix.mitre.org/XMLSchema/common/1.1.1/stix_common.xsd");
					put("http://stix.mitre.org/default_vocabularies-1",
							"http://stix.mitre.org/XMLSchema/default_vocabularies/1.1.1/stix_default_vocabularies.xsd");
					put("http://stix.mitre.org/extensions/AP#CAPEC2.7-1",
							"http://stix.mitre.org/XMLSchema/extensions/attack_pattern/capec_2.7/1.0.1/capec_2.7_attack_pattern.xsd");
					put("http://stix.mitre.org/extensions/Address#CIQAddress3.0-1",
							"http://stix.mitre.org/XMLSchema/extensions/address/ciq_3.0/1.1.1/ciq_3.0_address.xsd");
					put("http://stix.mitre.org/extensions/Identity#CIQIdentity3.0-1",
							"http://stix.mitre.org/XMLSchema/extensions/identity/ciq_3.0/1.1.1/ciq_3.0_identity.xsd");
					put("http://stix.mitre.org/extensions/Malware#MAEC4.1-1",
							"http://stix.mitre.org/XMLSchema/extensions/malware/maec_4.1/1.0.1/maec_4.1_malware.xsd");
					put("http://stix.mitre.org/extensions/StructuredCOA#Generic-1",
							"http://stix.mitre.org/XMLSchema/extensions/structured_coa/generic/1.1.1/generic_structured_coa.xsd");
					put("http://stix.mitre.org/extensions/TestMechanism#Generic-1",
							"http://stix.mitre.org/XMLSchema/extensions/test_mechanism/generic/1.1.1/generic_test_mechanism.xsd");
					put("http://stix.mitre.org/extensions/TestMechanism#OVAL5.10-1",
							"http://stix.mitre.org/XMLSchema/extensions/test_mechanism/oval_5.10/1.1.1/oval_5.10_test_mechanism.xsd");
					put("http://stix.mitre.org/extensions/TestMechanism#OpenIOC2010-1",
							"http://stix.mitre.org/XMLSchema/extensions/test_mechanism/open_ioc_2010/1.1.1/open_ioc_2010_test_mechanism.xsd");
					put("http://stix.mitre.org/extensions/TestMechanism#Snort-1",
							"http://stix.mitre.org/XMLSchema/extensions/test_mechanism/snort/1.1.1/snort_test_mechanism.xsd");
					put("http://stix.mitre.org/extensions/TestMechanism#YARA-1",
							"http://stix.mitre.org/XMLSchema/extensions/test_mechanism/yara/1.1.1/yara_test_mechanism.xsd");
					put("http://stix.mitre.org/extensions/Vulnerability#CVRF-1",
							"http://stix.mitre.org/XMLSchema/extensions/vulnerability/cvrf_1.1/1.1.1/cvrf_1.1_vulnerability.xsd");
					put("http://stix.mitre.org/stix-1",
							"http://stix.mitre.org/XMLSchema/core/1.1.1/stix_core.xsd");

					// Schema locations for namespaces defined by CybOX schemas
					put("http://cybox.mitre.org/cybox-2",
							"http://cybox.mitre.org/XMLSchema/core/2.1/cybox_core.xsd");
					put("http://cybox.mitre.org/common-2",
							"http://cybox.mitre.org/XMLSchema/common/2.1/cybox_common.xsd");
					put("http://cybox.mitre.org/default_vocabularies-2",
							"http://cybox.mitre.org/XMLSchema/default_vocabularies/2.1/cybox_default_vocabularies.xsd");
					put("http://cybox.mitre.org/objects#AccountObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Account/2.1/Account_Object.xsd");
					put("http://cybox.mitre.org/objects#AddressObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Address/2.1/Address_Object.xsd");
					put("http://cybox.mitre.org/objects#APIObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/API/2.1/API_Object.xsd");
					put("http://cybox.mitre.org/objects#ArchiveFileObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/Archive_File/1.0/Archive_File_Object.xsd");
					put("http://cybox.mitre.org/objects#ARPCacheObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/ARP_Cache/1.0/ARP_Cache_Object.xsd");
					put("http://cybox.mitre.org/objects#ArtifactObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Artifact/2.1/Artifact_Object.xsd");
					put("http://cybox.mitre.org/objects#ASObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/AS/1.0/AS_Object.xsd");
					put("http://cybox.mitre.org/objects#CodeObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Code/2.1/Code_Object.xsd");
					put("http://cybox.mitre.org/objects#CustomObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/Custom/1.1/Custom_Object.xsd");
					put("http://cybox.mitre.org/objects#DeviceObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Device/2.1/Device_Object.xsd");
					put("http://cybox.mitre.org/objects#DiskObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Disk/2.1/Disk_Object.xsd");
					put("http://cybox.mitre.org/objects#DiskPartitionObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Disk_Partition/2.1/Disk_Partition_Object.xsd");
					put("http://cybox.mitre.org/objects#DNSCacheObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/DNS_Cache/2.1/DNS_Cache_Object.xsd");
					put("http://cybox.mitre.org/objects#DNSQueryObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/DNS_Query/2.1/DNS_Query_Object.xsd");
					put("http://cybox.mitre.org/objects#DNSRecordObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/DNS_Record/2.1/DNS_Record_Object.xsd");
					put("http://cybox.mitre.org/objects#DomainNameObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/Domain_Name/1.0/Domain_Name_Object.xsd");
					put("http://cybox.mitre.org/objects#EmailMessageObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Email_Message/2.1/Email_Message_Object.xsd");
					put("http://cybox.mitre.org/objects#FileObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/File/2.1/File_Object.xsd");
					put("http://cybox.mitre.org/objects#GUIDialogboxObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/GUI_Dialogbox/2.1/GUI_Dialogbox_Object.xsd");
					put("http://cybox.mitre.org/objects#GUIObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/GUI/2.1/GUI_Object.xsd");
					put("http://cybox.mitre.org/objects#GUIWindowObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/GUI_Window/2.1/GUI_Window_Object.xsd");
					put("http://cybox.mitre.org/objects#HostnameObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/Hostname/1.0/Hostname_Object.xsd");
					put("http://cybox.mitre.org/objects#HTTPSessionObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/HTTP_Session/2.1/HTTP_Session_Object.xsd");
					put("http://cybox.mitre.org/objects#ImageFileObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/Image_File/1.0/Image_File_Object.xsd");
					put("http://cybox.mitre.org/objects#LibraryObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Library/2.1/Library_Object.xsd");
					put("http://cybox.mitre.org/objects#LinkObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/Link/1.1/Link_Object.xsd");
					put("http://cybox.mitre.org/objects#LinuxPackageObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Linux_Package/2.1/Linux_Package_Object.xsd");
					put("http://cybox.mitre.org/objects#MemoryObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Memory/2.1/Memory_Object.xsd");
					put("http://cybox.mitre.org/objects#MutexObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Mutex/2.1/Mutex_Object.xsd");
					put("http://cybox.mitre.org/objects#NetworkConnectionObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Network_Connection/2.1/Network_Connection_Object.xsd");
					put("http://cybox.mitre.org/objects#NetworkFlowObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Network_Flow/2.1/Network_Flow_Object.xsd");
					put("http://cybox.mitre.org/objects#NetworkRouteEntryObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Network_Route_Entry/2.1/Network_Route_Entry_Object.xsd");
					put("http://cybox.mitre.org/objects#NetworkRouteObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Network_Route/2.1/Network_Route_Object.xsd");
					put("http://cybox.mitre.org/objects#NetworkSocketObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Network_Socket/2.1/Network_Socket_Object.xsd");
					put("http://cybox.mitre.org/objects#NetworkSubnetObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Network_Subnet/2.1/Network_Subnet_Object.xsd");
					put("http://cybox.mitre.org/objects#PacketObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Network_Packet/2.1/Network_Packet_Object.xsd");
					put("http://cybox.mitre.org/objects#PDFFileObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/PDF_File/1.1/PDF_File_Object.xsd");
					put("http://cybox.mitre.org/objects#PipeObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Pipe/2.1/Pipe_Object.xsd");
					put("http://cybox.mitre.org/objects#PortObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Port/2.1/Port_Object.xsd");
					put("http://cybox.mitre.org/objects#ProcessObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Process/2.1/Process_Object.xsd");
					put("http://cybox.mitre.org/objects#ProductObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Product/2.1/Product_Object.xsd");
					put("http://cybox.mitre.org/objects#SemaphoreObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Semaphore/2.1/Semaphore_Object.xsd");
					put("http://cybox.mitre.org/objects#SMSMessageObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/SMS_Message/1.0/SMS_Message_Object.xsd");
					put("http://cybox.mitre.org/objects#SocketAddressObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/Socket_Address/1.1/Socket_Address_Object.xsd");
					put("http://cybox.mitre.org/objects#SystemObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/System/2.1/System_Object.xsd");
					put("http://cybox.mitre.org/objects#UnixFileObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Unix_File/2.1/Unix_File_Object.xsd");
					put("http://cybox.mitre.org/objects#UnixNetworkRouteEntryObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Unix_Network_Route_Entry/2.1/Unix_Network_Route_Entry_Object.xsd");
					put("http://cybox.mitre.org/objects#UnixPipeObject",
							"http://cybox.mitre.org/XMLSchema/objects/Unix_Pipe/2.1/Unix_Pipe_Object.xsd");
					put("http://cybox.mitre.org/objects#UnixProcessObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Unix_Process/2.1/Unix_Process_Object.xsd");
					put("http://cybox.mitre.org/objects#UnixUserAccountObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Unix_User_Account/2.1/Unix_User_Account_Object.xsd");
					put("http://cybox.mitre.org/objects#UnixVolumeObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Unix_Volume/2.1/Unix_Volume_Object.xsd");
					put("http://cybox.mitre.org/objects#URIObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/URI/2.1/URI_Object.xsd");
					put("http://cybox.mitre.org/objects#URLHistoryObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/URL_History/1.0/URL_History_Object.xsd");
					put("http://cybox.mitre.org/objects#UserAccountObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/User_Account/2.1/User_Account_Object.xsd");
					put("http://cybox.mitre.org/objects#VolumeObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Volume/2.1/Volume_Object.xsd");
					put("http://cybox.mitre.org/objects#WhoisObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Whois/2.1/Whois_Object.xsd");
					put("http://cybox.mitre.org/objects#WinComputerAccountObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Computer_Account/2.1/Win_Computer_Account_Object.xsd");
					put("http://cybox.mitre.org/objects#WinCriticalSectionObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Critical_Section/2.1/Win_Critical_Section_Object.xsd");
					put("http://cybox.mitre.org/objects#WinDriverObject-3",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Driver/3.0/Win_Driver_Object.xsd");
					put("http://cybox.mitre.org/objects#WinEventLogObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Event_Log/2.1/Win_Event_Log_Object.xsd");
					put("http://cybox.mitre.org/objects#WinEventObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Event/2.1/Win_Event_Object.xsd");
					put("http://cybox.mitre.org/objects#WinExecutableFileObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Executable_File/2.1/Win_Executable_File_Object.xsd");
					put("http://cybox.mitre.org/objects#WinFileObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_File/2.1/Win_File_Object.xsd");
					put("http://cybox.mitre.org/objects#WinFilemappingObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Filemapping/1.0/Win_Filemapping_Object.xsd");
					put("http://cybox.mitre.org/objects#WinHandleObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Handle/2.1/Win_Handle_Object.xsd");
					put("http://cybox.mitre.org/objects#WinHookObject-1",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Hook/1.0/Win_Hook_Object.xsd");
					put("http://cybox.mitre.org/objects#WinKernelHookObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Kernel_Hook/2.1/Win_Kernel_Hook_Object.xsd");
					put("http://cybox.mitre.org/objects#WinKernelObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Kernel/2.1/Win_Kernel_Object.xsd");
					put("http://cybox.mitre.org/objects#WinMailslotObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Mailslot/2.1/Win_Mailslot_Object.xsd");
					put("http://cybox.mitre.org/objects#WinMemoryPageRegionObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Memory_Page_Region/2.1/Win_Memory_Page_Region_Object.xsd");
					put("http://cybox.mitre.org/objects#WinMutexObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Mutex/2.1/Win_Mutex_Object.xsd");
					put("http://cybox.mitre.org/objects#WinNetworkRouteEntryObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Network_Route_Entry/2.1/Win_Network_Route_Entry_Object.xsd");
					put("http://cybox.mitre.org/objects#WinNetworkShareObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Network_Share/2.1/Win_Network_Share_Object.xsd");
					put("http://cybox.mitre.org/objects#WinPipeObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Pipe/2.1/Win_Pipe_Object.xsd");
					put("http://cybox.mitre.org/objects#WinPrefetchObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Prefetch/2.1/Win_Prefetch_Object.xsd");
					put("http://cybox.mitre.org/objects#WinProcessObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Process/2.1/Win_Process_Object.xsd");
					put("http://cybox.mitre.org/objects#WinRegistryKeyObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Registry_Key/2.1/Win_Registry_Key_Object.xsd");
					put("http://cybox.mitre.org/objects#WinSemaphoreObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Semaphore/2.1/Win_Semaphore_Object.xsd");
					put("http://cybox.mitre.org/objects#WinServiceObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Service/2.1/Win_Service_Object.xsd");
					put("http://cybox.mitre.org/objects#WinSystemObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_System/2.1/Win_System_Object.xsd");
					put("http://cybox.mitre.org/objects#WinSystemRestoreObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_System_Restore/2.1/Win_System_Restore_Object.xsd");
					put("http://cybox.mitre.org/objects#WinTaskObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Task/2.1/Win_Task_Object.xsd");
					put("http://cybox.mitre.org/objects#WinThreadObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Thread/2.1/Win_Thread_Object.xsd");
					put("http://cybox.mitre.org/objects#WinUserAccountObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_User_Account/2.1/Win_User_Account_Object.xsd");
					put("http://cybox.mitre.org/objects#WinVolumeObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Volume/2.1/Win_Volume_Object.xsd");
					put("http://cybox.mitre.org/objects#WinWaitableTimerObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/Win_Waitable_Timer/2.1/Win_Waitable_Timer_Object.xsd");
					put("http://cybox.mitre.org/objects#X509CertificateObject-2",
							"http://cybox.mitre.org/XMLSchema/objects/X509_Certificate/2.1/X509_Certificate_Object.xsd");

					// Schema locations for namespaces not defined by STIX, but
					// hosted on the STIX website
					put("urn:oasis:names:tc:ciq:xal:3",
							"http://stix.mitre.org/XMLSchema/external/oasis_ciq_3.0/xAL.xsd");
					put("urn:oasis:names:tc:ciq:xpil:3",
							"http://stix.mitre.org/XMLSchema/external/oasis_ciq_3.0/xPIL.xsd");
					put("urn:oasis:names:tc:ciq:xnl:3",
							"http://stix.mitre.org/XMLSchema/external/oasis_ciq_3.0/xNL.xsd");
				}
			});

	/**
	 * Adds the default schema locations to the document.
	 * 
	 * @param document
	 */
	public static Document addSchemaLocations(Document document) {
		String schemaLocation = "", namespace, location;
		Set<String> namespaces = new HashSet<String>();

		document.getDocumentElement().normalize();

		NamedNodeMap attributes = document.getDocumentElement().getAttributes();

		for (int i = 0; i < attributes.getLength(); i++) {
			Node attribute = attributes.item(i);

			if (XML_NAMESPACE.equals(attribute.getNamespaceURI())
					&& attribute.getNodeName().startsWith("xmlns:"))
				namespaces.add(attributes.item(i).getNodeValue());
		}

		for (Object object : namespaces) {
			namespace = (String) object;

			location = DEFAULT_XML_SCHEMA_LOCATIONS.get(namespace);

			if (location != null)
				schemaLocation += namespace + " " + location + " ";
		}

		schemaLocation = schemaLocation.trim();

		document.getDocumentElement().setAttributeNS(
				"http://www.w3.org/2001/XMLSchema-instance",
				"xsi:schemaLocation", schemaLocation);

		return document;
	}

	private interface ElementVisitor {

		void visit(Element element);

	}

	/**
	 * Used to transverse an XML document.
	 * 
	 * @param element
	 * @param visitor
	 */
	private final static void traverse(Element element, ElementVisitor visitor) {

		visitor.visit(element);

		NodeList children = element.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);

			if (node.getNodeType() != Node.ELEMENT_NODE)
				continue;

			traverse((Element) node, visitor);
		}
	}

	/**
	 * Returns a String for a JAXBElement
	 * 
	 * @param jaxbElement
	 * @return
	 */
	public static String getXMLString(JAXBElement<?> jaxbElement) {
		return getXMLString(jaxbElement, true);
	}
	
	/**
	 * Returns a String for a JAXBElement
	 * 
	 * @param jaxbElement
	 * @param prettyPrint
	 * @return
	 */
	public static String getXMLString(JAXBElement<?> jaxbElement, boolean prettyPrint) {
		
		try {
	        Document document = DocumentBuilderFactory
	                .newInstance().newDocumentBuilder().newDocument();
			
	        JAXBContext jaxbContext = JAXBContext
	                .newInstance(jaxbElement.getDeclaredType().getPackage().getName());
	
	        Marshaller marshaller = jaxbContext.createMarshaller();
	
	        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
	                true);
	
	        marshaller
	                .setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	
	        try {
	            marshaller.marshal(jaxbElement, document);
	        } catch (JAXBException e) {
	            // otherwise handle non-XMLRootElements
	            QName qualifiedName = new QName(
	                    Utilities.getnamespaceURI(jaxbElement), jaxbElement.getClass().getSimpleName());
	
	            @SuppressWarnings({ "rawtypes", "unchecked" })
	            JAXBElement root = new JAXBElement(
	                    qualifiedName, jaxbElement.getClass(), jaxbElement);
	
	            marshaller.marshal(root, document);
	        }
	
	        Utilities.removeUnusedNamespaces(document);
	
	        document = Utilities.addSchemaLocations(document);

	        return Utilities.getXMLString(document, prettyPrint);
        
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns a String for a Document.
	 * 
	 * @param document
	 * @return
	 */
	public static String getXMLString(Document document) {
		return getXMLString(document, true);
	}
	
	/**
	 * Returns a String for a Document.
	 * 
	 * @param document
	 * @param prettyPrint
	 * @return
	 */
	public static String getXMLString(Document document, boolean prettyPrint) {

		try {
			DOMImplementationRegistry registry = DOMImplementationRegistry
					.newInstance();
			DOMImplementationLS domImplementationLS = (DOMImplementationLS) registry
					.getDOMImplementation("LS");
			LSSerializer serializaer = domImplementationLS.createLSSerializer();

			if (prettyPrint) {
				serializaer.getDomConfig().setParameter("format-pretty-print",
						Boolean.TRUE);
				serializaer.getDomConfig().setParameter("xml-declaration",
						Boolean.TRUE);
			}

			// otherwise UTF-16 is used by default
			LSOutput lsOutput = domImplementationLS.createLSOutput();
			lsOutput.setEncoding("UTF-8");
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
			lsOutput.setByteStream(byteStream);

			serializaer.write(document, lsOutput);

			return new String(byteStream.toByteArray(), "UTF-8");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * JAXB adds all namespaces known by the JAXBContext to the root element of
	 * the XML document for performance reasons as per JAXB-103
	 * (http://java.net/
	 * jira/browse/JAXB-103?focusedCommentId=64411&page=com.atlassian
	 * .jira.plugin.system.issuetabpanels%3Acomment-tabpanel#action_64411).
	 * 
	 * This helper method based on Reboot's
	 * (http://stackoverflow.com/users/392730/reboot) response to a
	 * stackoverflow question on the subject will prune down the namespaces to
	 * only those used.
	 * 
	 * @param document
	 */
	public static void removeUnusedNamespaces(Document document) {
		final Set<String> namespaces = new HashSet<String>();

		Element element = document.getDocumentElement();

		traverse(element, new ElementVisitor() {

			public void visit(Element element) {
				String namespace = element.getNamespaceURI();

				if (namespace == null)
					namespace = "";

				namespaces.add(namespace);

				NamedNodeMap attributes = element.getAttributes();

				for (int i = 0; i < attributes.getLength(); i++) {
					Node attribute = attributes.item(i);

					if (XML_NAMESPACE.equals(attribute.getNamespaceURI()))
						continue;

					String prefix;

					if (XML_SCHEMA_INSTANCE.equals(attribute.getNamespaceURI())) {
						if ("type".equals(attribute.getLocalName())) {
							String value = attribute.getNodeValue();

							if (value.contains(":"))
								prefix = value.substring(0, value.indexOf(":"));
							else
								prefix = null;
						} else {
							continue;
						}
					} else {
						prefix = attribute.getPrefix();
					}

					namespace = element.lookupNamespaceURI(prefix);

					if (namespace == null)
						namespace = "";

					namespaces.add(namespace);
				}
			}

		});

		traverse(element, new ElementVisitor() {

			public void visit(Element element) {

				Set<String> removeLocalNames = new HashSet<String>();

				NamedNodeMap attributes = element.getAttributes();

				for (int i = 0; i < attributes.getLength(); i++) {
					Node attribute = attributes.item(i);

					if (!XML_NAMESPACE.equals(attribute.getNamespaceURI()))
						continue;

					if (namespaces.contains(attribute.getNodeValue()))
						continue;

					removeLocalNames.add(attribute.getLocalName());
				}

				for (String localName : removeLocalNames) {
					element.removeAttributeNS(XML_NAMESPACE, localName);
				}
			}

		});
	}

	/**
	 * Pull the namespace URI from the package for the class of the object.
	 * 
	 * @param obj
	 * @return
	 */
	public static String getnamespaceURI(Object obj) {

		Package pkg = obj.getClass().getPackage();

		XmlSchema xmlSchemaAnnotation = pkg.getAnnotation(XmlSchema.class);

		return xmlSchemaAnnotation.namespace();
	}
}
