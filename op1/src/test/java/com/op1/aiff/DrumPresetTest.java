package com.op1.aiff;

import com.op1.util.ComplianceCheck;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public class DrumPresetTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DrumPresetTest.class);

    @Test
    public void canReadDrumPreset1() throws Exception {
        doCanReadDrumPreset(ExampleFile.DRUM_PRESET_1.getFile());
    }

    @Test
    public void canReadDrumPreset2() throws Exception {
        doCanReadDrumPreset(ExampleFile.DRUM_PRESET_2.getFile());
    }

    @Test
    public void canReadDrumPreset3() throws Exception {
        doCanReadDrumPreset(ExampleFile.DRUM_PRESET_3.getFile());
    }

    @Test
    public void canReadDrumPreset4() throws Exception {
        doCanReadDrumPreset(ExampleFile.DRUM_PRESET_4.getFile());
    }

    @Test
    public void canReadDrumPreset5() throws Exception {
        doCanReadDrumPreset(ExampleFile.DRUM_PRESET_5.getFile());
    }

    @Test
    public void canReadDrumPreset6() throws Exception {
        doCanReadDrumPreset(ExampleFile.DRUM_PRESET_6.getFile());
    }

    @Test
    public void canReadDrumPreset7() throws Exception {
        doCanReadDrumPreset(ExampleFile.DRUM_PRESET_7.getFile());
    }

    @Test
    public void canReadDrumPreset8() throws Exception {
        doCanReadDrumPreset(ExampleFile.DRUM_PRESET_8.getFile());
    }

    private void doCanReadDrumPreset(File drumPreset) throws Exception {

        // given
        final AiffReader aiffReader = AiffReader.newAiffReader(drumPreset);

        // when
        final Aiff aiff = aiffReader.readAiff();
        LOGGER.debug(aiff.toString());

        // then
        assertThat(aiff, notNullValue());
        new ComplianceCheck(aiff).enforceCompliance();
    }
}
