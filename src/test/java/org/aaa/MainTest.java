package org.aaa;

import net.jqwik.api.Property;
import org.assertj.core.api.Assertions;

class MainTest {

	@Property
	void testMe() {
		Assertions.assertThat(true).isTrue();
	}
}