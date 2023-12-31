package ch.dvbern.stip.test.generator.api;

import ch.dvbern.oss.stip.contract.test.dto.*;
import ch.dvbern.stip.test.util.TestConstants;
import org.instancio.Instancio;
import org.instancio.Model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static ch.dvbern.stip.test.generator.api.model.gesuch.AdresseSpecModel.adresseSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.AusbildungUpdateDtoSpecModel.ausbildungUpdateDtoSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.AusbildungUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecAusbildungModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.AuszahlungUpdateDtoSpecModel.auszahlungUpdateDtoSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.AuszahlungUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecAuszahlungModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.EinnahmenKostenUpdateDtoSpecModel.einnahmenKostenUpdateDtoSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.EinnahmenKostenUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecEinnahmenKostenModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.ElternUpdateDtoSpecModel.elternUpdateDtoSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.ElternUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecElternsModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.FamiliensituationUpdateDtoSpecModel.familiensituationUpdateDtoSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.FamiliensituationUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecFamiliensituationModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.GeschwisterUpdateDtoSpecModel.geschwisterUpdateDtoSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.GeschwisterUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecGeschwistersModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.KindUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecKinderModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.KindUpdateDtoSpecModel.kinderUpdateDtoSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.LebenslaufItemUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecLebenslaufModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.LebenslaufItemUpdateDtoSpecModel.lebenslaufItemUpdateDtoSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.PartnerUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecPartnerModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.PartnerUpdateDtoSpecModel.partnerUpdateDtoSpecModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.PersonInAusbildungUpdateDtoSpecModel.gesuchFormularUpdateDtoSpecPersonInAusbildungModel;
import static ch.dvbern.stip.test.generator.api.model.gesuch.PersonInAusbildungUpdateDtoSpecModel.personInAusbildungUpdateDtoSpecModel;
import static org.instancio.Select.field;

public class GesuchTestSpecGenerator {

	private static final Model<GesuchFormularUpdateDtoSpec> gesuchFormularUpdateDtoSpecModelFull = Instancio.of(
					GesuchFormularUpdateDtoSpec.class)
			.set(
					field(GesuchFormularUpdateDtoSpec::getPersonInAusbildung),
					Instancio.of(personInAusbildungUpdateDtoSpecModel)
							.set(field(PersonInAusbildungUpdateDtoSpec::getAdresse), Instancio.create(adresseSpecModel))
							.set(field(PersonInAusbildungUpdateDtoSpec::getGeburtsdatum), LocalDate.of(2000, 10, 10))
							.set(field(PersonInAusbildungUpdateDtoSpec::getZivilstand), ZivilstandDtoSpec.VERHEIRATET)
							.create())
			.set(
					field(GesuchFormularUpdateDtoSpec::getFamiliensituation),
					Instancio.create(familiensituationUpdateDtoSpecModel)
			)
			.set(
					field(GesuchFormularUpdateDtoSpec::getAusbildung),
					Instancio.create(ausbildungUpdateDtoSpecModel))
			.set(
					field(GesuchFormularUpdateDtoSpec::getPartner),
					Instancio.create(partnerUpdateDtoSpecModel)
			)
			.set(
					field(GesuchFormularUpdateDtoSpec::getAuszahlung),
					Instancio.create(auszahlungUpdateDtoSpecModel)
			)
			.set(
					field(GesuchFormularUpdateDtoSpec::getGeschwisters),
					Instancio.create(geschwisterUpdateDtoSpecModel)
			)
			.set(
					field(GesuchFormularUpdateDtoSpec::getLebenslaufItems),
					Instancio.of(lebenslaufItemUpdateDtoSpecModel)
							.set(field(LebenslaufItemUpdateDtoSpec::getVon), "08.2016")
							.set(field(LebenslaufItemUpdateDtoSpec::getBis), "12.2021")
							.create()
			)
			.set(
					field(GesuchFormularUpdateDtoSpec::getElterns),
					Arrays.asList(
							Instancio.of(elternUpdateDtoSpecModel)
									.set(field(ElternUpdateDtoSpec::getElternTyp), ElternTypDtoSpec.VATER)
									.create()
									.get(0),
							Instancio.of(elternUpdateDtoSpecModel)
									.set(field(ElternUpdateDtoSpec::getElternTyp), ElternTypDtoSpec.MUTTER)
									.set(
											field(ElternUpdateDtoSpec::getSozialversicherungsnummer),
											TestConstants.AHV_NUMMER_VALID_MUTTER)
									.create()
									.get(0))
			)
			.set(
					field(GesuchFormularUpdateDtoSpec::getEinnahmenKosten),
					Instancio.of(einnahmenKostenUpdateDtoSpecModel)
							.set(field(EinnahmenKostenUpdateDtoSpec::getZulagen), BigDecimal.TEN)
							.set(field(EinnahmenKostenUpdateDtoSpec::getAlimente), null).create()
			)
			.set(
					field(GesuchFormularUpdateDtoSpec::getKinds),
					Instancio.create(kinderUpdateDtoSpecModel)
			)
			.toModel();

	public static final Model<GesuchTrancheUpdateDtoSpec> gesuchTrancheDtoSpecModel =
			Instancio.of(GesuchTrancheUpdateDtoSpec.class).toModel();
	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecFullModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecModelFull))
									.create()
					)
					.toModel();

	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecPersonInAusbildungModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecPersonInAusbildungModel))
									.create()
					)
					.toModel();

	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecFamiliensituationModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecFamiliensituationModel))
									.create()
					)
					.toModel();
	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecPartnerModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecPartnerModel))
									.create()
					)
					.toModel();
	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecAusbildungModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecAusbildungModel))
									.create()
					)
					.toModel();
	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecAuszahlungModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecAuszahlungModel))
									.create()
					)
					.toModel();

	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecGeschwisterModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecGeschwistersModel))
									.create()
					)
					.toModel();

	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecLebenslaufModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecLebenslaufModel))
									.create()
					)
					.toModel();

	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecElternsModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecElternsModel))
									.create()
					)
					.toModel();

	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecEinnahmenKostenModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
								Instancio.of(gesuchTrancheDtoSpecModel)
										.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecEinnahmenKostenModel))
										.create()
					)
					.toModel();

	public static final Model<GesuchUpdateDtoSpec> gesuchUpdateDtoSpecKinderModel =
			Instancio.of(GesuchUpdateDtoSpec.class)
					.set(
							field(GesuchUpdateDtoSpec::getGesuchTrancheToWorkWith),
							Instancio.of(gesuchTrancheDtoSpecModel)
									.set(field(GesuchTrancheUpdateDtoSpec::getGesuchFormular), Instancio.create(gesuchFormularUpdateDtoSpecKinderModel))
									.create()
					)
					.toModel();
}
