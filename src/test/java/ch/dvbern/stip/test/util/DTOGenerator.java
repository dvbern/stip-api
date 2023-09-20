package ch.dvbern.stip.test.util;

import ch.dvbern.oss.stip.contract.test.dto.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static ch.dvbern.stip.test.util.TestConstants.AHV_NUMMER_VALID;

public class DTOGenerator {

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForLebenslaufBildungsart() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var lebenslaufItem = new LebenslaufItemUpdateDtoSpec();
		lebenslaufItem.setBeschreibung("Test");
		lebenslaufItem.setBis("02.2022");
		lebenslaufItem.setVon("01.2022");
		lebenslaufItem.setBildungsart(BildungsartDtoSpec.FACHHOCHSCHULEN);
		lebenslaufItem.setWohnsitz(WohnsitzKantonDtoSpec.BE);
		gesuchformularToWorkWith.setLebenslaufItems(new ArrayList<>());
		gesuchformularToWorkWith.getLebenslaufItems().add(lebenslaufItem);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForPersonInAusbildung() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var personInAusbildung = new PersonInAusbildungUpdateDtoSpec();
		var adresseDTO = prepareAdresseUpdate();
		personInAusbildung.setAdresse(adresseDTO);
		personInAusbildung.setAnrede(AnredeDtoSpec.HERR);
		personInAusbildung.setEmail("test@test.ch");
		personInAusbildung.setGeburtsdatum(LocalDate.of(2000, 10, 10));
		personInAusbildung.setNachname("Tester");
		personInAusbildung.setVorname("Prosper");
		personInAusbildung.setNationalitaet(LandDtoSpec.CH);
		personInAusbildung.setTelefonnummer("078 888 88 88");
		personInAusbildung.setDigitaleKommunikation(true);
		personInAusbildung.setIdentischerZivilrechtlicherWohnsitz(true);
		personInAusbildung.setKorrespondenzSprache(SpracheDtoSpec.DEUTSCH);
		personInAusbildung.setSozialhilfebeitraege(false);
		personInAusbildung.setZivilstand(ZivilstandDtoSpec.LEDIG);
		personInAusbildung.setSozialversicherungsnummer(AHV_NUMMER_VALID);
		personInAusbildung.setQuellenbesteuert(false);
		personInAusbildung.setWohnsitz(WohnsitzDtoSpec.FAMILIE);
		personInAusbildung.setHeimatort("Bern");
		gesuchformularToWorkWith.setPersonInAusbildung(personInAusbildung);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static AdresseDtoSpec prepareAdresseUpdate() {
		var adresseDTO = new AdresseDtoSpec();
		adresseDTO.setLand(LandDtoSpec.CH);
		adresseDTO.setPlz("3000");
		adresseDTO.setOrt("Bern");
		adresseDTO.setStrasse("Strasse");
		return adresseDTO;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForAusbildung() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var ausbildung = new AusbildungUpdateDtoSpec();
		ausbildung.setAusbildungBegin("01.2022");
		ausbildung.setAusbildungEnd("02.2022");
		ausbildung.setAusbildungNichtGefunden(false);
		ausbildung.setPensum(AusbildungsPensumDtoSpec.VOLLZEIT);
		ausbildung.setAusbildungsgangId(UUID.fromString("3a8c2023-f29e-4466-a2d7-411a7d032f42"));
		ausbildung.setFachrichtung("test");
		gesuchformularToWorkWith.setAusbildung(ausbildung);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForFamiliensituation() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var familiensituation = new FamiliensituationUpdateDtoSpec();
		familiensituation.setObhut(ElternschaftsteilungDtoSpec.GEMEINSAM);
		familiensituation.setObhutMutter(new BigDecimal(50));
		familiensituation.setObhutVater(new BigDecimal(50));
		familiensituation.setElternVerheiratetZusammen(false);
		familiensituation.setElternteilUnbekanntVerstorben(false);
		familiensituation.setGerichtlicheAlimentenregelung(false);
		familiensituation.setMutterWiederverheiratet(false);
		familiensituation.setVaterWiederverheiratet(false);
		familiensituation.setSorgerecht(ElternschaftsteilungDtoSpec.GEMEINSAM);
		gesuchformularToWorkWith.setFamiliensituation(familiensituation);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForPartner() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var partner = new PartnerUpdateDtoSpec();
		partner.setAdresse(prepareAdresseUpdate());
		partner.setGeburtsdatum(LocalDate.of(2002, 12, 1));
		partner.setNachname("Testname");
		partner.setVorname("Testvorname");
		partner.setSozialversicherungsnummer(AHV_NUMMER_VALID);
		partner.setJahreseinkommen(new BigDecimal(100000));
		gesuchformularToWorkWith.setPartner(partner);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForAuszahlung() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var auszahlung = new AuszahlungUpdateDtoSpec();
		auszahlung.setAdresse(prepareAdresseUpdate());
		auszahlung.setNachname("Testname");
		auszahlung.setVorname("Testvorname");
		auszahlung.setIban("IBAN TODO");
		auszahlung.setKontoinhaber(KontoinhaberDtoSpec.GESUCHSTELLER);
		gesuchformularToWorkWith.setAuszahlung(auszahlung);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForGeschwister() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var geschwister = new GeschwisterUpdateDtoSpec();
		geschwister.setNachname("Geschwisternachname");
		geschwister.setVorname("Geschwistervorname");
		geschwister.setGeburtsdatum(LocalDate.of(2001, 1, 1));
		geschwister.setAusbildungssituation(AusbildungssituationDtoSpec.IN_AUSBILDUNG);
		geschwister.setWohnsitz(WohnsitzDtoSpec.MUTTER_VATER);
		geschwister.setWohnsitzAnteilMutter(new BigDecimal(40));
		geschwister.setWohnsitzAnteilVater(new BigDecimal(60));
		gesuchformularToWorkWith.setGeschwisters(new ArrayList<>());
		gesuchformularToWorkWith.getGeschwisters().add(geschwister);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForEltern() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var eltern = prepareElternWithElternTyp(ElternTypDtoSpec.VATER);
		gesuchformularToWorkWith.setElterns(new ArrayList<>());
		gesuchformularToWorkWith.getElterns().add(eltern);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static ElternUpdateDtoSpec prepareElternWithElternTyp(ElternTypDtoSpec elternTypDtoSpec) {
		var eltern = new ElternUpdateDtoSpec();
		eltern.setNachname("Elternnachname");
		eltern.setVorname("Elternvorname");
		eltern.setGeburtsdatum(LocalDate.of(2001, 1, 1));
		eltern.setTelefonnummer("");
		eltern.setAusweisbFluechtling(false);
		eltern.setAdresse(prepareAdresseUpdate());
		eltern.setElternTyp(elternTypDtoSpec);
		eltern.setErgaenzungsleistungAusbezahlt(false);
		eltern.setIdentischerZivilrechtlicherWohnsitz(false);
		eltern.setIdentischerZivilrechtlicherWohnsitzOrt("Test");
		eltern.setIdentischerZivilrechtlicherWohnsitzPLZ("1234");
		eltern.setSozialhilfebeitraegeAusbezahlt(false);
		eltern.setSozialversicherungsnummer(AHV_NUMMER_VALID);
		return eltern;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForKind() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var kind = new KindUpdateDtoSpec();
		kind.setNachname("Kindnachname");
		kind.setVorname("Kindvorname");
		kind.setGeburtsdatum(LocalDate.of(2011, 1, 1));
		kind.setAusbildungssituation(AusbildungssituationDtoSpec.IN_AUSBILDUNG);
		kind.setWohnsitz(WohnsitzDtoSpec.MUTTER_VATER);
		kind.setWohnsitzAnteilMutter(new BigDecimal(40));
		kind.setWohnsitzAnteilVater(new BigDecimal(60));
		gesuchformularToWorkWith.setKinds(new ArrayList<>());
		gesuchformularToWorkWith.getKinds().add(kind);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateForEinnhamenKosten() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		var einnahmenKosten = new EinnahmenKostenUpdateDtoSpec();
		einnahmenKosten.setNettoerwerbseinkommen(new BigDecimal(50000));
		einnahmenKosten.setFahrkosten(new BigDecimal(2000));
		einnahmenKosten.setWohnkosten(new BigDecimal(20000));
		einnahmenKosten.setPersonenImHaushalt(new BigDecimal(10));
		einnahmenKosten.setVerdienstRealisiert(false);
		gesuchformularToWorkWith.setEinnahmenKosten(einnahmenKosten);
		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static GesuchUpdateDtoSpec prepareGesuchUpdateVollstaendigt() {
		var gesuchUpdatDTO = new GesuchUpdateDtoSpec();
		var gesuchformularToWorkWith = new GesuchFormularUpdateDtoSpec();
		gesuchformularToWorkWith.setPersonInAusbildung(prepareGesuchUpdateForPersonInAusbildung().getGesuchFormularToWorkWith().getPersonInAusbildung());
		gesuchformularToWorkWith.setAusbildung(prepareGesuchUpdateForAusbildung().getGesuchFormularToWorkWith().getAusbildung());
		gesuchformularToWorkWith.setFamiliensituation(prepareGesuchUpdateForFamiliensituation().getGesuchFormularToWorkWith().getFamiliensituation());
		gesuchformularToWorkWith.setEinnahmenKosten(prepareGesuchUpdateForEinnhamenKosten().getGesuchFormularToWorkWith().getEinnahmenKosten());
		gesuchformularToWorkWith.setElterns(prepareGesuchUpdateForEltern().getGesuchFormularToWorkWith().getElterns());
		gesuchformularToWorkWith.setAuszahlung(prepareGesuchUpdateForAuszahlung().getGesuchFormularToWorkWith().getAuszahlung());
		gesuchformularToWorkWith.setLebenslaufItems(prepareGesuchUpdateForLebenslaufBildungsart().getGesuchFormularToWorkWith().getLebenslaufItems());
		// Gescuh muss eingereicht werden können
		gesuchformularToWorkWith.getEinnahmenKosten().setZulagen(new BigDecimal(1000));
		gesuchformularToWorkWith.getEinnahmenKosten().setWillDarlehen(false);
		gesuchformularToWorkWith.getLebenslaufItems().get(0).setVon("08.2016");
		gesuchformularToWorkWith.getLebenslaufItems().get(0).setBis("12.2021");
		gesuchformularToWorkWith.getEinnahmenKosten().setAusbildungskostenTertiaerstufe(BigDecimal.TEN);
		// add eltern TODO
		gesuchformularToWorkWith.getElterns().add(prepareElternWithElternTyp(ElternTypDtoSpec.MUTTER));

		gesuchUpdatDTO.setGesuchFormularToWorkWith(gesuchformularToWorkWith);
		return gesuchUpdatDTO;
	}

	public static AusbildungsgangUpdateDtoSpec prepareAusbildungsgangUpdate() {
		var ausbildungsgangUpdate = new AusbildungsgangUpdateDtoSpec();
		ausbildungsgangUpdate.setAusbildungsort(AusbildungsortDtoSpec.BERN);
		ausbildungsgangUpdate.setAusbildungsrichtung(BildungsartDtoSpec.UNIVERSITAETEN_ETH);
		ausbildungsgangUpdate.setBezeichnungDe("Bachelor Informatik");
		ausbildungsgangUpdate.setBezeichnungFr("Bachelor Informatik");
		ausbildungsgangUpdate.setAusbildungsstaette(prepareAusbildungsstaetteUpdateDto());
		return ausbildungsgangUpdate;
	}

	public static AusbildungsstaetteUpdateDtoSpec prepareAusbildungsstaetteUpdateDto() {
		var	ausbidlungsstaetteUpdateDto = new AusbildungsstaetteUpdateDtoSpec();
		ausbidlungsstaetteUpdateDto.setNameDe("Uni Bern");
		ausbidlungsstaetteUpdateDto.setNameFr("Uni Bern");
		return ausbidlungsstaetteUpdateDto;
	}

}
