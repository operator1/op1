package com.op1.drumkit;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class DrumkitMetaTest {

    @Test
    public void canGetDrumkitMetaFromJson() throws Exception {

        // given
        String json = "{\"drum_version\":1,\"type\":\"drum\",\"name\":\"user\",\"octave\":0,\"pitch\":[0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0],\"start\":[0,7182626,28251665,36651686,70791481,77312657,80023389,92838493,169018974,191199899,196954117,278771133,326436180,345062314,349416528,366788745,386214301,401861877,492245292,603563832,654296713,756257548,764073220,811600296],\"end\":[7178568,28247607,36647628,70787423,77308599,80019331,92834435,169014916,191195841,196950059,278767075,326432122,345058256,349412470,366784687,386210243,401857819,492241234,603559774,654292655,756253490,764069162,811596238,1065763831],\"playmode\":[8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192],\"reverse\":[8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192],\"volume\":[8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192,8192],\"dyna_env\":[0,8192,0,8192,0,0,0,0],\"fx_active\":false,\"fx_type\":\"delay\",\"fx_params\":[8000,8000,8000,8000,8000,8000,8000,8000],\"lfo_active\":false,\"lfo_type\":\"tremolo\",\"lfo_params\":[16000,16000,16000,16000,0,0,0,0]}  ";

        // when
        DrumkitMeta meta = DrumkitMeta.fromJson(json);

        // then
        assertThat(meta, notNullValue());
        assertThat(meta.getDrumVersion(), equalTo(1));
        assertThat(meta.getType(), equalTo("drum"));
        assertThat(meta.getName(), equalTo("user"));
        assertThat(meta.getOctave(), equalTo(0));
        assertIsArrayOfSize24(meta.getPitch());
        assertIsArrayOfSize24(meta.getStart());
        assertIsArrayOfSize24(meta.getEnd());
        assertIsArrayOfSize24(meta.getPlaymode());
        assertIsArrayOfSize24(meta.getReverse());
        assertIsArrayOfSize24(meta.getVolume());
        assertIsArrayOfSize8(meta.getDynaEnv());
        assertThat(meta.isFxActive(), is(false));
        assertThat(meta.getFxType(), equalTo("delay"));
        assertIsArrayOfSize8(meta.getFxParams());
        assertThat(meta.isLfoActive(), is(false));
        assertThat(meta.getLfoType(), equalTo("tremolo"));
        assertIsArrayOfSize8(meta.getLfoParams());
    }

    private void assertIsArrayOfSize24(int[] array) {
        assertThat(array, notNullValue());
        assertThat(array.length, equalTo(24));
    }

    private void assertIsArrayOfSize8(int[] array) {
        assertThat(array, notNullValue());
        assertThat(array.length, equalTo(8));
    }
}
