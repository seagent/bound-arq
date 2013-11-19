package tr.edu.ege.seagent.boundarq.util;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.sparql.vocabulary.FOAF;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class SampleQueries {

	private static final String CHEBI_VIRTUOSO_ENDPOINT_URL = "http://155.223.24.47:8892/chebi/sparql";
	private static final String KEGG_VIRTUOSO_ENDPOINT_URL = "http://155.223.24.47:8894/kegg/sparql";
	private static final String DRUGBANK_VIRTUOSO_ENDPOINT_URL = "http://155.223.24.47:8893/drugbank/sparql";
	private static final String SWDOGFOOD_VIRTUOSO_ENDPOINT_URL = "http://155.223.24.47:8898/swdogfood/sparql";
	private static final String JAMENDO_VIRTUOSO_ENDPOINT_URL = "http://155.223.24.47:8896/jamendo/sparql";
	private static final String GEONAMES_VIRTUOSO_ENDPOINT_URL = "http://155.223.24.47:8895/geonames/sparql";
	private static final String DBPEDIA_VIRTUOSO_ENDPOINT_URL = "http://155.223.24.47:8897/dbpedia/sparql";
	private static final String NYTIMES_VIRTUOSO_ENDPOINT_URL = "http://155.223.24.47:8891/nytimes/sparql";
	private static final String LMDB_VIRTUOSO_ENDPOINT_URL = "http://155.223.24.47:8899/lmdb/sparql";

	private static final String CHEBI_4STORE_ENDPOINT_URL = "http://155.223.25.212:3000/sparql/";
	private static final String KEGG_4STORE_ENDPOINT_URL = "http://155.223.25.212:4000/sparql/";
	private static final String DRUGBANK_4STORE_ENDPOINT_URL = "http://155.223.25.212:8000/sparql/";
	private static final String SWDOGFOOD_4STORE_ENDPOINT_URL = "http://155.223.25.212:5500/sparql/";
	private static final String JAMENDO_4STORE_ENDPOINT_URL = "http://155.223.25.212:5000/sparql/";
	private static final String GEONAMES_4STORE_ENDPOINT_URL = "http://155.223.25.212:2000/sparql/";
	private static final String DBPEDIA_4STORE_ENDPOINT_URL = "http://155.223.25.212:7000/sparql/";
	private static final String NYTIMES_4STORE_ENDPOINT_URL = "http://155.223.25.212:9000/sparql/";
	private static final String LMDB_4STORE_ENDPOINT_URL = "http://155.223.25.212:2500/sparql/";

	private static final String CHEBI_ENDPOINT_URL = CHEBI_4STORE_ENDPOINT_URL;
	private static final String KEGG_ENDPOINT_URL = KEGG_4STORE_ENDPOINT_URL;
	private static final String DRUGBANK_ENDPOINT_URL = DRUGBANK_4STORE_ENDPOINT_URL;
	private static final String SWDOGFOOD_ENDPOINT_URL = SWDOGFOOD_4STORE_ENDPOINT_URL;
	private static final String JAMENDO_ENDPOINT_URL = JAMENDO_4STORE_ENDPOINT_URL;
	private static final String GEONAMES_ENDPOINT_URL = GEONAMES_4STORE_ENDPOINT_URL;
	private static final String DBPEDIA_ENDPOINT_URL = DBPEDIA_4STORE_ENDPOINT_URL;
	private static final String NYTIMES_ENDPOINT_URL = NYTIMES_4STORE_ENDPOINT_URL;
	private static final String LMDB_ENDPOINT_URL = LMDB_4STORE_ENDPOINT_URL;
	public static final String FEDERATED_CROSS_DOMAIN_4 = "SELECT  ?actor ?news WHERE {"
			+ "SERVICE <"
			+ LMDB_ENDPOINT_URL
			+ "> "
			+ "{?film <http://purl.org/dc/terms/title> \"Tarzan\". "
			+ "?film <http://data.linkedmdb.org/resource/movie/actor> ?actor. "
			+ "?actor <http://www.w3.org/2002/07/owl#sameAs> ?x. }"
			+ "SERVICE <"
			+ NYTIMES_ENDPOINT_URL
			+ "> {"
			+ "?y <http://data.nytimes.com/elements/topicPage> ?news. "
			+ "?y <http://www.w3.org/2002/07/owl#sameAs> ?x. }}";
	public static final String FEDERATED_CROSS_DOMAIN_2 = "SELECT  ?party ?page WHERE {"
			+ "SERVICE <"
			+ DBPEDIA_ENDPOINT_URL
			+ "> "
			+ "{<http://dbpedia.org/resource/Barack_Obama> <http://dbpedia.org/ontology/party> ?party. }"
			+ "SERVICE <"
			+ NYTIMES_ENDPOINT_URL
			+ "> "
			+ "{?x <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama>. "
			+ "?x <http://data.nytimes.com/elements/topicPage> ?page. }}";

	public static final String FEDERATED_CROSS_DOMAIN_3 = "SELECT  ?president ?party ?page WHERE {"
			+ "SERVICE <"
			+ DBPEDIA_ENDPOINT_URL
			+ "> "
			+ "{?president <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/President>. "
			+ "?president <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/United_States>. "
			+ "?president <http://dbpedia.org/ontology/party> ?party. }"
			+ "SERVICE <"
			+ NYTIMES_ENDPOINT_URL
			+ "> "
			+ "{?x <http://www.w3.org/2002/07/owl#sameAs> ?president. "
			+ "?x <http://data.nytimes.com/elements/topicPage> ?page. }}";

	public static final String FEDERATED_CROSS_DOMAIN_1 = "SELECT  ?predicate ?object WHERE {"
			+ "{SERVICE <"
			+ DBPEDIA_ENDPOINT_URL
			+ "> "
			+ "{<http://dbpedia.org/resource/Barack_Obama> ?predicate ?object. }} "
			+ "UNION {SERVICE <"
			+ NYTIMES_ENDPOINT_URL
			+ "> "
			+ "{?subject <http://www.w3.org/2002/07/owl#sameAs> <http://dbpedia.org/resource/Barack_Obama>. "
			+ "?subject ?predicate ?object. }}}";

	public static final String FEDERATED_CROSS_DOMAIN_5 = "SELECT  ?film ?director ?genre WHERE {"
			+ "SERVICE <"
			+ DBPEDIA_ENDPOINT_URL
			+ "> "
			+ "{?director <http://dbpedia.org/ontology/nationality> <http://dbpedia.org/resource/Italy>. "
			+ "?film <http://dbpedia.org/ontology/director> ?director. }"
			+ "SERVICE <"
			+ LMDB_ENDPOINT_URL
			+ "> "
			+ "{?x <http://www.w3.org/2002/07/owl#sameAs> ?film. "
			+ "?x <http://data.linkedmdb.org/resource/movie/genre> ?genre. }}";

	public static final String FEDERATED_CROSS_DOMAIN_6 = "SELECT  ?name ?location WHERE {"
			+ "SERVICE <"
			+ GEONAMES_ENDPOINT_URL
			+ "> "
			+ "{?germany <http://www.geonames.org/ontology#name> \"Federal Republic of Germany\". "
			+ "?location <http://www.geonames.org/ontology#parentFeature> ?germany. }"
			+ "{ BIND(<"
			+ SWDOGFOOD_ENDPOINT_URL
			+ "> AS ?ser808419)} "
			+ "UNION "
			+ "{ BIND(<"
			+ JAMENDO_ENDPOINT_URL
			+ "> AS ?ser808419)} "
			+ "UNION "
			+ "{ BIND(<"
			+ KEGG_ENDPOINT_URL
			+ "> AS ?ser808419)} "
			+ " SERVICE ?ser808419 "
			+ "{?artist <http://xmlns.com/foaf/0.1/based_near> ?location. }"
			+ "{ BIND(<"
			+ SWDOGFOOD_ENDPOINT_URL
			+ "> AS ?ser697073)} "
			+ "UNION "
			+ "{ BIND(<"
			+ JAMENDO_ENDPOINT_URL
			+ "> AS ?ser697073)}  "
			+ "SERVICE ?ser697073 {"
			+ "?artist <http://xmlns.com/foaf/0.1/name> ?name. }}";

	public static final String FEDERATED_CROSS_DOMAIN_7 = "SELECT  ?location ?news WHERE {"
			+ "SERVICE <"
			+ GEONAMES_ENDPOINT_URL
			+ "> "
			+ "{?parent <http://www.geonames.org/ontology#name> \"California\". "
			+ "?location <http://www.geonames.org/ontology#parentFeature> ?parent. }"
			+ "SERVICE <"
			+ NYTIMES_ENDPOINT_URL
			+ "> "
			+ "{?y <http://www.w3.org/2002/07/owl#sameAs> ?location. "
			+ "?y <http://data.nytimes.com/elements/topicPage> ?news. }}";

	public static final String FEDERATED_LIFE_SCIENCES_1 = "SELECT ?drug ?melt WHERE {"
			+ "{SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/meltingPoint> ?melt. }} "
			+ "UNION {{"
			+ "?drug <http://dbpedia.org/ontology/Drug/meltingPoint> ?melt. }}}";

	public static final String FEDERATED_LIFE_SCIENCES_2 = "SELECT  ?predicate ?object WHERE {"
			+ "{SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object. }} "
			+ "UNION {SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff. }"
			+ "{ BIND(<"
			+ DBPEDIA_ENDPOINT_URL
			+ "> AS ?ser998816)}  "
			+ "UNION { BIND(<"
			+ KEGG_ENDPOINT_URL
			+ "> AS ?ser998816)}  "
			+ "UNION { BIND(<"
			+ CHEBI_ENDPOINT_URL
			+ "> AS ?ser998816)} "
			+ "UNION { BIND(<"
			+ DRUGBANK_ENDPOINT_URL
			+ "> AS ?ser998816)}  SERVICE ?ser998816 "
			+ "{?caff ?predicate ?object. }}}";

	public static final String FEDERATED_CONSTRUCT_LIFE_SCIENCES_2 = "CONSTRUCT  {"
			+ "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object."
			+ "<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff."
			+ "?caff ?predicate ?object."
			+ "} WHERE {"
			+ "{SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object. }} "
			+ "UNION {SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff. }"
			+ "{ BIND(<"
			+ DBPEDIA_ENDPOINT_URL
			+ "> AS ?ser998816)}  "
			+ "UNION { BIND(<"
			+ KEGG_ENDPOINT_URL
			+ "> AS ?ser998816)}  "
			+ "UNION { BIND(<"
			+ CHEBI_ENDPOINT_URL
			+ "> AS ?ser998816)} "
			+ "UNION { BIND(<"
			+ DRUGBANK_ENDPOINT_URL
			+ "> AS ?ser998816)}  SERVICE ?ser998816 "
			+ "{?caff ?predicate ?object. }}}";

	public static final String FEDERATED_ASK_LIFE_SCIENCES_2 = "ASK  {"
			+ "{SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object. }} "
			+ "UNION {SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff. }"
			+ "{ BIND(<" + DBPEDIA_ENDPOINT_URL + "> AS ?ser998816)}  "
			+ "UNION { BIND(<" + KEGG_ENDPOINT_URL + "> AS ?ser998816)}  "
			+ "UNION { BIND(<" + CHEBI_ENDPOINT_URL + "> AS ?ser998816)} "
			+ "UNION { BIND(<" + DRUGBANK_ENDPOINT_URL
			+ "> AS ?ser998816)}  SERVICE ?ser998816 "
			+ "{?caff ?predicate ?object. }}}";

	public static final String FEDERATED_DESCRIBE_LIFE_SCIENCES_2 = "DESCRIBE ?caff  {"
			+ "{SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> ?predicate ?object. }} "
			+ "UNION {SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{<http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugs/DB00201> <http://www.w3.org/2002/07/owl#sameAs> ?caff. }"
			+ "{ BIND(<"
			+ DBPEDIA_ENDPOINT_URL
			+ "> AS ?ser998816)}  "
			+ "UNION { BIND(<"
			+ KEGG_ENDPOINT_URL
			+ "> AS ?ser998816)}  "
			+ "UNION { BIND(<"
			+ CHEBI_ENDPOINT_URL
			+ "> AS ?ser998816)} "
			+ "UNION { BIND(<"
			+ DRUGBANK_ENDPOINT_URL
			+ "> AS ?ser998816)}  SERVICE ?ser998816 "
			+ "{?caff ?predicate ?object. }}}";

	public static final String FEDERATED_LIFE_SCIENCES_3 = "SELECT  ?Drug ?IntDrug ?IntEffect WHERE {"
			+ "SERVICE <"
			+ DBPEDIA_ENDPOINT_URL
			+ "> "
			+ "{?Drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Drug>. }"
			+ "SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{?y <http://www.w3.org/2002/07/owl#sameAs> ?Drug. "
			+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug1> ?y. "
			+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/interactionDrug2> ?IntDrug. "
			+ "?Int <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/text> ?IntEffect. }}";

	public static final String FEDERATED_LIFE_SCIENCES_4 = "SELECT  ?drugDesc ?cpd ?equation WHERE {"
			+ "SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/cathartics>. "
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?cpd. "
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/description> ?drugDesc. }"
			+ "SERVICE <"
			+ KEGG_ENDPOINT_URL
			+ "> "
			+ "{?enzyme <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Enzyme>. "
			+ "?enzyme <http://bio2rdf.org/ns/kegg#xSubstrate> ?cpd. "
			+ "?reaction <http://bio2rdf.org/ns/kegg#xEnzyme> ?enzyme. "
			+ "?reaction <http://bio2rdf.org/ns/kegg#equation> ?equation. }}";

	public static final String FEDERATED_LIFE_SCIENCES_5 = "SELECT  ?drug ?keggUrl ?chebiImage WHERE {"
			+ "SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{?drug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugs>. "
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/keggCompoundId> ?keggDrug. "
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/genericName> ?drugBankName. }"
			+ "{ BIND(<"
			+ KEGG_ENDPOINT_URL
			+ "> AS ?ser350577)} UNION "
			+ "{ BIND(<"
			+ CHEBI_ENDPOINT_URL
			+ "> AS ?ser350577)}  SERVICE ?ser350577 "
			+ "{?keggDrug <http://bio2rdf.org/ns/bio2rdf#url> ?keggUrl. }"
			+ "SERVICE <"
			+ CHEBI_ENDPOINT_URL
			+ "> "
			+ "{?chebiDrug <http://purl.org/dc/elements/1.1/title> ?drugBankName. "
			+ "?chebiDrug <http://bio2rdf.org/ns/bio2rdf#image> ?chebiImage. }}";

	public static final String FEDERATED_LIFE_SCIENCES_6 = "SELECT  ?drug ?title WHERE {"
			+ "SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/drugCategory> <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugcategory/micronutrient>. "
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/casRegistryNumber> ?id. }"
			+ "SERVICE <"
			+ KEGG_ENDPOINT_URL
			+ "> "
			+ "{?keggDrug <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://bio2rdf.org/ns/kegg#Drug>. "
			+ "?keggDrug <http://bio2rdf.org/ns/bio2rdf#xRef> ?id. "
			+ "?keggDrug <http://purl.org/dc/elements/1.1/title> ?title. }}";

	public static final String FEDERATED_LIFE_SCIENCES_7 = "SELECT  ?drug ?transform ?mass WHERE {"
			+ "SERVICE <"
			+ DRUGBANK_ENDPOINT_URL
			+ "> "
			+ "{?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/affectedOrganism> \"Humans and other mammals\". "
			+ "?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/casRegistryNumber> ?cas. "
			+ "OPTIONAL {?drug <http://www4.wiwiss.fu-berlin.de/drugbank/resource/drugbank/biotransformation> ?transform. }}"
			+ "SERVICE <"
			+ KEGG_ENDPOINT_URL
			+ "> "
			+ "{?keggDrug <http://bio2rdf.org/ns/bio2rdf#xRef> ?cas. "
			+ "?keggDrug <http://bio2rdf.org/ns/bio2rdf#mass> ?mass. "
			+ "FILTER ( ?mass > '5' )" + "}}";

	private static final String DBPEDIA_SERVICE_TAG = "<http://dbpedia.org/sparql>";

	private static final String SERVICE = "SERVICE ";

	private static final String LMDB_SERVICE_TAG = "<http://data.linkedmdb.org/sparql>";

	private static final String NYTIMES_SERVICE_TAG = "<http://155.223.24.47:8891/nytimes/sparql>";

	private static final String LMDB_SERVICE_BLOCK = SERVICE + LMDB_SERVICE_TAG;

	private static final String PREFIXES = "PREFIX dbpedia: <http://dbpedia.org/ontology/>"
			+ "PREFIX rdfs:"
			+ "<"
			+ RDFS.getURI()
			+ ">"
			+ "PREFIX owl:"
			+ "<"
			+ OWL.getURI()
			+ ">"
			+ "PREFIX foaf:"
			+ "<"
			+ FOAF.getURI()
			+ ">"
			+ "PREFIX rdf:"
			+ "<"
			+ RDF.getURI()
			+ ">"
			+ "PREFIX dcterms: <http://purl.org/dc/terms/>"
			+ "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>"
			+ "PREFIX dbprop: <http://dbpedia.org/property/>"
			+ "PREFIX dbpedia: <http://dbpedia.org/ontology/>";
	private static final String DBPEDIA_SERVICE_BLOCK = SERVICE
			+ DBPEDIA_SERVICE_TAG;

	public static String TRIPLE_CHAIN_DBPEDIA_MOVIE_QUERY = PREFIXES
			+ "SELECT "
			+ "?createdMovie "
			+ "?producer "
			+ "?producerName "
			+ "WHERE { "
			+ DBPEDIA_SERVICE_BLOCK
			+ "{<http://dbpedia.org/resource/Amy_Irving> <http://dbpedia.org/property/spouse> ?directorDBP. "
			+ "?createdMovie <http://dbpedia.org/ontology/creator> ?directorDBP."
			+ " }"
			+ DBPEDIA_SERVICE_BLOCK
			+ "{?createdMovie <http://dbpedia.org/ontology/producer> ?producer.}"
			+ DBPEDIA_SERVICE_BLOCK + "{?producer foaf:name ?producerName.}"
			+ "}";

	public static String TWO_VARIABLE_DOUBLE_CHAIN_DBPEDIA_MOVIE_QUERY = PREFIXES
			+ "SELECT "
			+ "?directorDBP "
			+ "?createdMovieName "
			+ "?editedMovieName "
			+ "WHERE { "
			+ DBPEDIA_SERVICE_BLOCK
			+ "{<http://dbpedia.org/resource/Amy_Irving> <http://dbpedia.org/property/spouse> ?directorDBP. "
			+ "?createdMovie <http://dbpedia.org/ontology/creator> ?directorDBP."
			+ "?editedMovie <http://dbpedia.org/ontology/editing> ?directorDBP."
			+ " }"
			+ DBPEDIA_SERVICE_BLOCK
			+ "{?createdMovie foaf:name ?createdMovieName."
			+ "?editedMovie foaf:name ?editedMovieName.}" + "}";

	public static String DOUBLE_CHAIN_DBPEDIA_LMDB_MOVIE_QUERY = PREFIXES
			+ "SELECT "
			+ "?movieName "
			+ "WHERE { "
			+ DBPEDIA_SERVICE_BLOCK
			+ "{<http://dbpedia.org/resource/Amy_Irving> <http://dbpedia.org/property/spouse> ?directorDBP. "
			+ "?movie dbpedia:director ?directorDBP. }" + DBPEDIA_SERVICE_BLOCK
			+ "{?movie foaf:name ?movieName.}" + "}";

	public static String TWO_BIND_SERVICE_UNION_MOVIE_QUERY = PREFIXES
			+ "SELECT "
			+ "?subsidiary "
			+ "WHERE { "
			+ " {BIND("
			+ DBPEDIA_SERVICE_TAG
			+ " AS ?ser0)}"
			+ " UNION {BIND("
			+ DBPEDIA_SERVICE_TAG
			+ " AS ?ser0)}"
			+ " SERVICE ?ser0"
			+ "{?subsidiary <http://dbpedia.org/ontology/keyPerson> <http://dbpedia.org/resource/Steven_Spielberg>.}"
			+ "}";

	public static String DOUBLE_CHAIN_BIND_SERVICE_DOUBLE_UNION_MOVIE_QUERY = PREFIXES
			+ "SELECT "
			+ "?subsidiary ?subsidiaryName "
			+ "WHERE { "
			+ " {"
			+ "{BIND("
			+ DBPEDIA_SERVICE_TAG
			+ " AS ?ser0)}"
			+ " UNION {BIND("
			+ NYTIMES_SERVICE_TAG
			+ " AS ?ser0)}"
			+ " SERVICE ?ser0"
			+ "{?subsidiary <http://dbpedia.org/ontology/keyPerson> <http://dbpedia.org/resource/Steven_Spielberg>.}"
			+ "}"
			+ "{"
			+ "{BIND("
			+ DBPEDIA_SERVICE_TAG
			+ " AS ?ser1)}"
			+ " UNION {BIND("
			+ NYTIMES_SERVICE_TAG
			+ " AS ?ser1)}"
			+ " SERVICE ?ser1"
			+ "{?subsidiary foaf:name ?subsidiaryName.}"
			+ "}}";

	public static String OPTIONAL_DOUBLE_SERVICE_QUERY = PREFIXES
			+ "SELECT "
			+ "?editedMovie ?movieName "
			+ "WHERE { "
			+ DBPEDIA_SERVICE_BLOCK
			+ "{?editedMovie <http://dbpedia.org/ontology/editing> <http://dbpedia.org/resource/Steven_Spielberg>. "
			+ "}"
			+ DBPEDIA_SERVICE_BLOCK
			+ "{"
			+ "OPTIONAL "
			+ "{?editedMovie rdf:type <http://dbpedia.org/class/yago/AmericanFilms>.}"
			+ "{?editedMovie foaf:name ?movieName.}" + "}}";

	public static String OPTIONAL_DOUBLE_SERVICE_BIND_QUERY = PREFIXES
			+ "SELECT "
			+ "?editedMovie ?movieName "
			+ "WHERE { "
			+ DBPEDIA_SERVICE_BLOCK
			+ "{?editedMovie <http://dbpedia.org/ontology/editing> <http://dbpedia.org/resource/Steven_Spielberg>. "
			+ "}"
			+ "{BIND("
			+ DBPEDIA_SERVICE_TAG
			+ " AS ?ser1)}"
			+ " UNION {BIND("
			+ DBPEDIA_SERVICE_TAG
			+ " AS ?ser1)}"
			+ " SERVICE ?ser1"
			+ "{"
			+ "OPTIONAL "
			+ "{?editedMovie rdf:type <http://dbpedia.org/class/yago/AmericanFilms>.}"
			+ "{?editedMovie foaf:name ?movieName.}" + "}}";

	public static String DOUBLE_CHAIN_UNION_QUERY = PREFIXES
			+ "SELECT "
			+ "?directorDBP "
			+ "?developedWork "
			+ "?organization "
			+ "WHERE { "
			+ DBPEDIA_SERVICE_BLOCK
			+ "{<http://dbpedia.org/resource/Amy_Irving> <http://dbpedia.org/property/spouse> ?directorDBP. }"
			+ DBPEDIA_SERVICE_BLOCK
			+ "{"
			+ "{?developedWork <http://dbpedia.org/ontology/developer> ?directorDBP.}"
			+ "UNION"
			+ "{?organization <http://dbpedia.org/ontology/keyPerson> ?directorDBP.}"
			+ "}}";
	public static String DOUBLE_CHAIN_UNION_BIND_SERVICE_QUERY = PREFIXES
			+ "SELECT "
			+ "?directorDBP "
			+ "?developedWork "
			+ "?organization "
			+ "WHERE { "
			+ DBPEDIA_SERVICE_BLOCK
			+ "{<http://dbpedia.org/resource/Amy_Irving> <http://dbpedia.org/property/spouse> ?directorDBP. }"
			+ "{BIND("
			+ DBPEDIA_SERVICE_TAG
			+ " AS ?ser1)}"
			+ " UNION {BIND("
			+ DBPEDIA_SERVICE_TAG
			+ " AS ?ser1)}"
			+ " SERVICE ?ser1"
			+ "{"
			+ "{?developedWork <http://dbpedia.org/ontology/developer> ?directorDBP.}"
			+ "UNION"
			+ "{?organization <http://dbpedia.org/ontology/keyPerson> ?directorDBP.}"
			+ "}}";

	public static String DOUBLE_CHAIN_FILTER_QUERY = PREFIXES
			+ "SELECT "
			+ "?city "
			+ "?populationMetro "
			+ "WHERE { "
			+ DBPEDIA_SERVICE_BLOCK
			+ "{?city <http://dbpedia.org/ontology/country> <http://dbpedia.org/resource/Turkey>. "
			+ "?city dbpedia:type <http://dbpedia.org/resource/Metropolitan_municipality>."
			+ "?city <http://dbpedia.org/ontology/isPartOf> <http://dbpedia.org/resource/Aegean_Region>."
			+ "}"
			+ DBPEDIA_SERVICE_BLOCK
			+ "{"
			+ "?city <http://dbpedia.org/ontology/populationMetro> ?populationMetro."
			+ "FILTER(?populationMetro>100000)" + "}}";

	public static String DOUBLE_CHAIN_BIG_SOLUTION_QUERY = PREFIXES
			+ "SELECT "
			+ "?film "
			+ "?filmName "
			+ "WHERE { "
			+ DBPEDIA_SERVICE_BLOCK
			+ "{?film <http://dbpedia.org/ontology/distributor> <http://dbpedia.org/resource/DreamWorks>. "
			+ "}" + DBPEDIA_SERVICE_BLOCK + "{" + "?film foaf:name ?filmName."
			+ "}" + "}";

	public static final String CROSS_DOMAIN_QUERY_1 = "PREFIX dbpedia:<http://dbpedia.org/resource/> "
			+ "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "SELECT ?predicate ?object WHERE {"
			+ "{ SERVICE <http://dbpedia.org/sparql>"
			+ "{ dbpedia:Barack_Obama ?predicate ?object .}"
			+ "}"
			+ "UNION"
			+ "{ "
			+ "{ BIND(<http://el.dbpedia.org/sparql> AS ?ser1) }"
			+ "UNION"
			+ "{ BIND(<http://dbpedia.org/sparql> AS ?ser1) }"
			+ "UNION"
			+ "{ BIND(<http://155.223.24.47:8890/sparql> AS ?ser1) }"
			+ "{ ?subject owl:sameAs dbpedia:Barack_Obama ."
			+ "?subject ?predicate ?object .} " + "}" + "}";
	public static final String TWO_FILTER_VARIABLE_QUERY_DBPEDIA = PREFIXES
			+ "SELECT * WHERE { "
			+ "SERVICE <http://dbpedia.org/sparql>{ "
			+ "<http://dbpedia.org/resource/Hook_(film)> dbpedia:writer ?writer."
			+ "<http://dbpedia.org/resource/Hook_(film)> dcterms:subject ?subject. }"
			+ "SERVICE <http://dbpedia.org/sparql>{ "
			+ "?writer foaf:name ?writerName." + "?subject rdfs:label ?label."
			+ "}}";

	public static final String TWO_FILTER_VARIABLE_QUERY_LMDB = PREFIXES
			+ "SELECT * WHERE { "
			+ "SERVICE <http://data.linkedmdb.org/sparql>{ "
			+ "<http://data.linkedmdb.org/resource/film/10761> <http://data.linkedmdb.org/resource/movie/actor> ?actor."
			+ "<http://data.linkedmdb.org/resource/film/10761> <http://data.linkedmdb.org/resource/movie/film_story_contributor> ?storyCon. }"
			+ "SERVICE <http://data.linkedmdb.org/sparql>{ "
			+ "?actor rdfs:label ?actorLabel."
			+ "?storyCon rdfs:label ?scLabel." + "}}";
	public static final String TWO_FILTER_VARIABLE_QUERY_LOCAL_DBPEDIA = PREFIXES
			+ " SELECT * WHERE { "
			+ "SERVICE <"
			+ DBPEDIA_ENDPOINT_URL
			+ "> {"
			+ "<http://dbpedia.org/resource/Tetrahydrocannabinol> <http://www.w3.org/2004/02/skos/core#subject> ?subject	."
			+ "<http://dbpedia.org/resource/Tetrahydrocannabinol> <http://www.w3.org/2000/01/rdf-schema#label> ?label. }"
			+ " SERVICE <"
			+ DBPEDIA_ENDPOINT_URL
			+ "> { "
			+ "?drug rdfs:label ?label."
			+ "?subject <http://www.w3.org/2004/02/skos/core#broader> ?broader. }}";

}
