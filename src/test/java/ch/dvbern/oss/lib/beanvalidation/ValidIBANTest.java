/*
 * Copyright 2017 DV Bern AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * limitations under the License.
 */

package ch.dvbern.oss.lib.beanvalidation;

import javax.validation.Valid;

import ch.dvbern.oss.lib.beanvalidation.embeddables.IBAN;
import org.junit.Assert;
import org.junit.Test;

import static ch.dvbern.oss.lib.beanvalidation.util.ValidationTestHelper.assertNotViolated;
import static ch.dvbern.oss.lib.beanvalidation.util.ValidationTestHelper.assertViolated;

/**
 * Testklasse für {@link ValidIBANNummer}
 */
public class ValidIBANTest {

	@Test
	public void testValidIBAN() {
		Bean withoutIban = new Bean();
		assertNotViolated(ValidIBANNummer.class, withoutIban, "iban");

		Bean bean = new Bean();
		bean.setIban(new IBAN("1234567890123456789"));
		assertViolated(ValidIBANNummer.class, bean, "iban");

		bean = new Bean();
		bean.setIban(new IBAN("CH123456"));
		assertViolated(ValidIBANNummer.class, bean, "iban");

		bean.setIban(new IBAN("CH3909000000306638172"));
		assertNotViolated(ValidIBANNummer.class, bean, "iban");

		bean.setIban(new IBAN("CH39 0900 0000 3066 3817 2"));
		assertNotViolated(ValidIBANNummer.class, bean, "iban");

		bean.setIban(new IBAN("CHE3909000000306638172"));
		assertViolated(ValidIBANNummer.class, bean, "iban");

		bean.setIban(new IBAN(""));
		assertViolated(ValidIBANNummer.class, bean, "iban");

		bean.setIban(new IBAN("NL53ABNA0205986478"));
		assertNotViolated(ValidIBANNummer.class, bean, "iban");
	}

	@Test
	public void testClearingNumberExtraction() {

		Bean bean = new Bean();
		bean.setIban(new IBAN("NL53ABNA0205986478"));
		assertNotViolated(ValidIBANNummer.class, bean, "iban");
		bean.getIban().extractClearingNumber();
		bean.getIban().extractClearingNumberWithoutLeadingZeros();
		Assert.assertEquals(bean.getIban().extractClearingNumberWithoutLeadingZeros(), bean.getIban()
				.extractClearingNumber());

		bean.setIban(new IBAN("CH39 0900 0000 3066 3817 2"));
		assertNotViolated(ValidIBANNummer.class, bean, "iban");
		String leadingZeroes = bean.getIban().extractClearingNumber();
		String noLeadingZeroes = bean.getIban().extractClearingNumberWithoutLeadingZeros();
		Assert.assertEquals("09000", leadingZeroes);
		Assert.assertEquals("9000", noLeadingZeroes);

		bean = new Bean();
		bean.setIban(new IBAN("CH123456"));
		assertViolated(ValidIBANNummer.class, bean, "iban");

		try {
			bean.getIban().extractClearingNumber();
			Assert.fail("Invalid Iban should trigger exception when calling extractClearingNumber");
		} catch (IllegalArgumentException e) {
			//expected
		}

		try {
			bean.getIban().extractClearingNumberWithoutLeadingZeros();
			Assert.fail("Invalid Iban should trigger exception when calling extractClearingNumber");
		} catch (IllegalArgumentException e) {
			//expected
		}
		

	}

	public static class Bean {

		@Valid
		private IBAN iban;

		public IBAN getIban() {
			return iban;
		}

		public void setIban(IBAN iban) {
			this.iban = iban;
		}
	}
}
