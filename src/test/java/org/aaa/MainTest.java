package org.aaa;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.WithNull;
import org.assertj.core.api.Assertions;

class MainTest {

	@Property
	void testMe(@ForAll @WithNull(1) Object nothing) {
		Assertions.assertThat(nothing).isNull();
	}

}