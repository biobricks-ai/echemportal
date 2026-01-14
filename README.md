# eChemPortal Data Sources Reference

This repository documents the data sources aggregated by [OECD eChemPortal](https://www.echemportal.org/), the global portal to information on chemical substances.

**Note:** Rather than scraping eChemPortal's search interface, we recommend building bricks directly from the individual source databases listed below, as they contain the actual data.

## Data Sources (35 total)

### Sources with Endpoint Data (Toxicity Values)

These sources have actual toxicity study results available through eChemPortal's Property Search:

| Source | Substances | Endpoints | URL | BioBrick |
|--------|------------|-----------|-----|----------|
| **ECHA REACH** | 26,673 | 1,346,246 | https://echa.europa.eu/information-on-chemicals | `echa-reach-study-results` |
| **CCR** (Canada) | 22,421 | 196,657 | https://pollution-waste.canada.ca/substances-search/ | TODO |
| **OECD SIDS IUCLID** | 242 | 23,436 | https://hpvchemicals.oecd.org/UI/SIDS_Details.aspx | TODO |
| **J-CHECK** | 2,297 | 5,065 | https://www.nite.go.jp/chem/jcheck/top.action | TODO |

### Substance Registries and Chemical Information

These sources provide substance identification, classifications, and links to safety information:

| Source | Substances | URL | Description | BioBrick |
|--------|------------|-----|-------------|----------|
| **CompTox Dashboard** | 1,218,247 | https://comptox.epa.gov/dashboard | EPA computational toxicology | Partial |
| **IGS (GESTIS)** | 223,691 | https://www.dguv.de/ifa/gestis/gestis-stoffdatenbank/ | German hazardous substances database | TODO |
| **SPIN** | 30,891 | https://spin2000.net/ | Nordic substances in products | TODO |
| **AICIS assessments** | 18,640 | https://www.industrialchemicals.gov.au/ | Australian industrial chemicals | TODO |
| **US EPA SRS** | 17,843 | https://iaspub.epa.gov/sor_internet/registry/substreg/home/overview/home.do | EPA Substance Registry Services | TODO |
| **ChemInfo** | 16,450 | https://www.cheminfo.de/ | German Federal chemical information | TODO |
| **U.S. EPA ECOTOX** | 13,079 | https://cfpub.epa.gov/ecotox/ | Ecotoxicology knowledgebase | `ecotox` |
| **EFSA OpenFoodTox** | 11,907 | https://www.efsa.europa.eu/en/data-report/chemical-hazards-database-openfoodtox | EU food safety chemical hazards | `openfoodtox` |
| **IPCHEM** | 7,148 | https://ipchem.jrc.ec.europa.eu/ | EU chemical monitoring platform | TODO |
| **INERIS-PSC** | 6,197 | https://substances.ineris.fr/ | French chemical substances portal | TODO |
| **HSDB** | 5,816 | https://pubchem.ncbi.nlm.nih.gov/source/hsdb | Hazardous Substances Data Bank | TODO |
| **HSNO CCID** | 5,452 | https://www.epa.govt.nz/database-search/chemical-classification-and-information-database-ccid/ | New Zealand hazardous substances | TODO |
| **OECD HPV** | 5,038 | https://hpvchemicals.oecd.org/ | High Production Volume chemicals | TODO |
| **ECHA C&L inventory** | 4,741 | https://echa.europa.eu/information-on-chemicals/cl-inventory-database | EU Classification & Labelling | TODO |
| **NITE Japan-GHS** | 3,376 | https://www.nite.go.jp/chem/english/ghs/ghs_index.html | Japanese GHS classifications | `japan-nite-ghs` |
| **EnviChem** | 2,548 | https://wwwp.ymparisto.fi/scripts/Kemrek/Kemrek_En.asp | Finnish environmental chemicals | TODO |
| **CESAR** | 2,518 | https://www.canada.ca/en/health-canada/services/chemical-substances/chemicals-management-plan/science/existing-substances-assessment-repository.html | Canada existing substances assessments | TODO |
| **INCHEM** | 2,343 | https://inchem.org/ | IPCS chemical safety documents | TODO |
| **ICSC** | 1,699 | https://www.ilo.org/safework/info/publications/lang--en/index.htm | ILO International Chemical Safety Cards | TODO |
| **ETOX** | 1,449 | https://webetox.uba.de/webETOX/ | German ecotoxicology database | TODO |
| **EPA OPPALB** | 630 | https://www.epa.gov/pesticide-science-and-assessing-pesticide-risks/aquatic-life-benchmarks-and-ecological-risk | EPA Aquatic Life Benchmarks | TODO |
| **US EPA IRIS** | 563 | https://www.epa.gov/iris | Integrated Risk Information System | `iris` |
| **Combined Exposures** | 540 | https://www.oecd.org/chemicalsafety/risk-assessment/case-studies-on-combined-exposure-to-chemicals.htm | OECD combined exposure case studies | TODO |
| **JECDB** | 471 | https://dra4.nihs.go.jp/mhlw_data/jsp/SearchPageENG.jsp | Japan Existing Chemical Database | TODO |
| **EPA HHBP** | 431 | https://www.epa.gov/pesticide-science-and-assessing-pesticide-risks/human-health-benchmarks-pesticides-hhbp | Human Health Benchmarks for Pesticides | TODO |
| **ECHA Biocides** | 381 | https://echa.europa.eu/information-on-chemicals/biocidal-active-substances | EU biocidal active substances | TODO |
| **AGRITOX** | 276 | https://www.anses.fr/en/content/agritox-database | French pesticides database | TODO |
| **UK CCRMP Outputs** | 164 | https://www.gov.uk/government/collections/risk-assessment-of-substances | UK chemical risk assessments | TODO |
| **OECD PFASs Fact Cards** | 106 | https://www.oecd.org/chemicalsafety/portal-perfluorinated-chemicals/ | PFAS information portal | TODO |
| **APVMA-CR** | 97 | https://apvma.gov.au/node/26596 | Australian pesticides reviews | TODO |

## Summary Statistics

From eChemPortal (as of January 2026):
- **Total substances:** 1,654,365
- **Total endpoints:** 1,571,404
- **Total synonyms:** 905,614

### Endpoint Distribution by Source

| Source | Endpoints | % of Total |
|--------|-----------|------------|
| ECHA REACH | 1,346,246 | 85.7% |
| CCR | 196,657 | 12.5% |
| OECD SIDS IUCLID | 23,436 | 1.5% |
| J-CHECK | 5,065 | 0.3% |

### Endpoint Types Available

**Toxicological:**
- Acute toxicity (oral, dermal, inhalation)
- Skin/eye irritation and corrosion
- Sensitisation
- Repeated dose toxicity
- Genetic toxicity (in vitro, in vivo)
- Carcinogenicity
- Reproductive/developmental toxicity
- Toxicokinetics

**Ecotoxicological:**
- Aquatic toxicity (fish, invertebrates, algae)
- Terrestrial toxicity
- Sediment toxicity

**Environmental Fate:**
- Biodegradation
- Bioaccumulation
- Hydrolysis
- Phototransformation

**Physical-Chemical:**
- Melting/boiling point
- Density, solubility
- Vapor pressure
- Partition coefficient
- Flammability

## References

- [eChemPortal Home](https://www.echemportal.org/)
- [eChemPortal Sources](https://www.echemportal.org/echemportal/content/participants)
- [OECD Harmonised Templates](https://www.oecd.org/en/topics/sub-issues/assessment-of-chemicals/harmonised-templates.html)
