import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PVector;
import xyscope.*;

public class ATest extends PApplet {

	XYscope xy;
	float[][][] coords = {{{86.26882f,89.91397f},{68.77205f,89.91397f},{51.275276f,89.91397f},{33.778503f,89.91397f},{16.281729f,89.91397f},{-1.2150459f,89.91397f},{-18.71182f,89.91397f},{-36.208595f,89.91397f},{-53.705368f,89.91397f},{-71.20214f,89.91397f},{-88.69893f,89.91397f},{-93.55914f,103.522575f},{-98.41936f,117.13118f},{-103.27957f,130.73978f},{-108.139786f,144.34837f},{-113.0f,157.95697f},{-117.860214f,171.56557f},{-122.72043f,185.17416f},{-127.58064f,198.78276f},{-132.44086f,212.39136f},{-137.30109f,226.0f},{-143.37636f,226.0f},{-149.45163f,226.0f},{-155.5269f,226.0f},{-161.60217f,226.0f},{-167.67744f,226.0f},{-173.75272f,226.0f},{-179.82799f,226.0f},{-185.90326f,226.0f},{-191.97853f,226.0f},{-198.05377f,226.0f},{-182.01506f,180.8f},{-165.97635f,135.6f},{-149.93764f,90.40001f},{-133.89893f,45.20001f},{-117.860214f,7.6293945E-6f},{-101.8215f,-45.199993f},{-85.78279f,-90.399994f},{-69.74408f,-135.59999f},{-53.705368f,-180.79999f},{-37.666668f,-226.0f},{-30.133335f,-226.0f},{-22.600002f,-226.0f},{-15.066669f,-226.0f},{-7.5333366f,-226.0f},{-3.8146973E-6f,-226.0f},{7.533329f,-226.0f},{15.066662f,-226.0f},{22.599995f,-226.0f},{30.133327f,-226.0f},{37.666664f,-226.0f},{53.705376f,-180.8f},{69.74409f,-135.6f},{85.7828f,-90.40001f},{101.82151f,-45.20001f},{117.86022f,-7.6293945E-6f},{133.89893f,45.199993f},{149.93764f,90.399994f},{165.97635f,135.59999f},{182.01506f,180.79999f},{198.05377f,226.0f},{191.49248f,226.0f},{184.93118f,226.0f},{178.36989f,226.0f},{171.8086f,226.0f},{165.2473f,226.0f},{158.686f,226.0f},{152.12471f,226.0f},{145.56342f,226.0f},{139.00212f,226.0f},{132.44087f,226.0f},{127.82367f,212.3914f},{123.20647f,198.7828f},{118.589264f,185.17421f},{113.97206f,171.56561f},{109.35486f,157.95702f},{104.737656f,144.34842f},{100.12045f,130.73982f},{95.50325f,117.13122f},{90.88605f,103.52261f},{86.26882f,89.91397f},{86.26882f,89.91397f},{69.25807f,41.31183f},{62.45377f,20.655914f},{55.649467f,0.0f},{48.845165f,-20.655914f},{42.040863f,-41.31183f},{35.23656f,-61.967743f},{28.432259f,-82.62366f},{21.627956f,-103.27957f},{14.823654f,-123.935486f},{8.019352f,-144.5914f},{1.2150536f,-165.24731f},{-6.3182793f,-144.5914f},{-13.851612f,-123.935486f},{-21.384945f,-103.27957f},{-28.918278f,-82.62366f},{-36.45161f,-61.967743f},{-43.984943f,-41.31183f},{-51.518276f,-20.655914f},{-59.05161f,0.0f},{-66.584946f,20.655914f},{-74.11828f,41.31183f},{-59.780643f,41.31183f},{-45.44301f,41.31183f},{-31.105373f,41.31183f},{-16.767738f,41.31183f},{-2.4301043f,41.31183f},{11.90753f,41.31183f},{26.245163f,41.31183f},{40.582798f,41.31183f},{54.920433f,41.31183f},{69.25807f,41.31183f},{69.25807f,41.31183f}}, {{-168.89249f,226.0f},{-168.89249f,180.8f},{-168.89249f,135.6f},{-168.89249f,90.40001f},{-168.89249f,45.20001f},{-168.89249f,7.6293945E-6f},{-168.89249f,-45.199993f},{-168.89249f,-90.399994f},{-168.89249f,-135.59999f},{-168.89249f,-180.79999f},{-168.89249f,-226.0f},{-150.42368f,-226.0f},{-131.95486f,-226.0f},{-113.486046f,-226.0f},{-95.01723f,-226.0f},{-76.54841f,-226.0f},{-58.07959f,-226.0f},{-39.61077f,-226.0f},{-21.141953f,-226.0f},{-2.6731339f,-226.0f},{15.7957f,-226.0f},{28.140648f,-225.68408f},{39.902367f,-224.73634f},{51.08086f,-223.15677f},{61.67613f,-220.94537f},{71.68817f,-218.10214f},{81.11699f,-214.62709f},{89.962585f,-210.5202f},{98.22495f,-205.7815f},{105.90409f,-200.41095f},{113.00001f,-194.4086f},{119.92581f,-187.8959f},{126.12259f,-180.9944f},{131.59033f,-173.70407f},{136.32904f,-166.02493f},{140.33871f,-157.95697f},{143.61935f,-149.5002f},{146.17096f,-140.6546f},{147.99355f,-131.4202f},{149.0871f,-121.79697f},{149.45163f,-111.78494f},{148.81981f,-98.10344f},{146.92433f,-85.24817f},{143.7652f,-73.21914f},{139.3424f,-62.016342f},{133.65596f,-51.639782f},{126.70585f,-42.08946f},{118.49208f,-33.36537f},{109.014656f,-25.467524f},{98.273575f,-18.395914f},{86.26882f,-12.150543f},{98.85678f,-7.023017f},{110.37549f,-1.3608673f},{120.82495f,4.835906f},{130.20517f,11.567304f},{138.51614f,18.833324f},{145.75786f,26.633968f},{151.93033f,34.969234f},{157.03355f,43.839127f},{161.06754f,53.24364f},{164.03226f,63.182785f},{164.9557f,65.63719f},{165.78195f,68.140205f},{166.51099f,70.69182f},{167.14282f,73.29203f},{167.67744f,75.94085f},{168.11487f,78.63827f},{168.4551f,81.384285f},{168.6981f,84.17891f},{168.84392f,87.02213f},{168.89249f,89.91397f},{168.89249f,90.42429f},{168.89249f,90.983215f},{168.89249f,91.590744f},{168.89249f,92.24687f},{168.89249f,92.9516f},{168.89249f,93.70493f},{168.89249f,94.50687f},{168.89249f,95.35741f},{168.89249f,96.256546f},{168.89249f,97.2043f},{168.55228f,108.188385f},{167.53163f,118.78365f},{165.83057f,128.9901f},{163.44907f,138.80772f},{160.38713f,148.23654f},{156.64478f,157.27654f},{152.22198f,165.92772f},{147.11876f,174.19008f},{141.33511f,182.06363f},{134.87097f,189.54839f},{127.31334f,196.4742f},{119.221085f,202.67097f},{110.5942f,208.13872f},{101.43269f,212.87743f},{91.736565f,216.8871f},{81.50581f,220.16774f},{70.74044f,222.71936f},{59.440437f,224.54195f},{47.605812f,225.6355f},{35.236565f,226.0f},{14.823658f,226.0f},{-5.5892487f,226.0f},{-26.002155f,226.0f},{-46.415062f,226.0f},{-66.82797f,226.0f},{-87.240875f,226.0f},{-107.65378f,226.0f},{-128.06668f,226.0f},{-148.47958f,226.0f},{-168.89249f,226.0f},{-168.89249f,226.0f},{-110.56989f,-31.5914f},{-99.391396f,-31.5914f},{-88.2129f,-31.5914f},{-77.0344f,-31.5914f},{-65.8559f,-31.5914f},{-54.677406f,-31.5914f},{-43.49891f,-31.5914f},{-32.32041f,-31.5914f},{-21.141916f,-31.5914f},{-9.963421f,-31.5914f},{1.2150574f,-31.5914f},{9.744735f,-31.737207f},{17.836992f,-32.174625f},{25.49183f,-32.903656f},{32.70925f,-33.9243f},{39.489254f,-35.23656f},{45.831837f,-36.84043f},{51.737f,-38.735916f},{57.204742f,-40.923016f},{62.235065f,-43.401726f},{66.82796f,-46.17206f},{71.44516f,-50.24249f},{75.57634f,-54.677437f},{79.221504f,-59.4769f},{82.380646f,-64.64088f},{85.053764f,-70.16937f},{87.24086f,-76.062386f},{88.94193f,-82.319916f},{90.15698f,-88.94196f},{90.88602f,-95.92852f},{91.129036f,-103.27957f},{90.886024f,-110.60635f},{90.15699f,-117.52001f},{88.94193f,-124.02055f},{87.24086f,-130.10797f},{85.053764f,-135.78227f},{82.380646f,-141.04346f},{79.221504f,-145.89153f},{75.57634f,-150.32648f},{71.44515f,-154.34831f},{66.82796f,-157.957f},{62.235058f,-161.18904f},{57.20474f,-164.08087f},{51.737f,-166.63249f},{45.83184f,-168.84389f},{39.48926f,-170.71507f},{32.709263f,-172.24605f},{25.491844f,-173.4368f},{17.837006f,-174.28734f},{9.744747f,-174.79767f},{1.2150574f,-174.96774f},{-9.963438f,-174.96774f},{-21.141933f,-174.96774f},{-32.320427f,-174.96774f},{-43.498924f,-174.96774f},{-54.67742f,-174.96774f},{-65.85592f,-174.96774f},{-77.03442f,-174.96774f},{-88.21291f,-174.96774f},{-99.39141f,-174.96774f},{-110.56989f,-174.96774f},{-110.56989f,-160.63011f},{-110.56989f,-146.29248f},{-110.56989f,-131.95485f},{-110.56989f,-117.61722f},{-110.56989f,-103.27959f},{-110.56989f,-88.941956f},{-110.56989f,-74.604324f},{-110.56989f,-60.26669f},{-110.56989f,-45.929054f},{-110.56989f,-31.5914f},{-110.56989f,-31.5914f},{110.56989f,99.63441f},{110.13248f,88.84473f},{108.82022f,78.83268f},{106.633125f,69.59827f},{103.57119f,61.14149f},{99.63442f,53.46235f},{94.822815f,46.560844f},{89.13637f,40.436974f},{82.57508f,35.090736f},{75.138954f,30.522133f},{66.82796f,26.73117f},{63.40151f,25.34601f},{59.926456f,24.106657f},{56.4028f,23.01311f},{52.830547f,22.065369f},{49.20969f,21.263433f},{45.54023f,20.607306f},{41.822166f,20.096983f},{38.055504f,19.732468f},{34.24024f,19.513758f},{30.37635f,19.440857f},{16.281727f,19.440857f},{2.1871023f,19.440857f},{-11.907522f,19.440857f},{-26.002148f,19.440857f},{-40.09677f,19.440857f},{-54.191395f,19.440857f},{-68.28602f,19.440857f},{-82.380646f,19.440857f},{-96.47527f,19.440857f},{-110.56989f,19.440857f},{-110.56989f,35.236557f},{-110.56989f,51.032257f},{-110.56989f,66.82796f},{-110.56989f,82.62366f},{-110.56989f,98.41936f},{-110.56989f,114.21506f},{-110.56989f,130.01076f},{-110.56989f,145.80646f},{-110.56989f,161.60216f},{-110.56989f,177.39786f},{-96.475266f,177.39786f},{-82.38064f,177.39786f},{-68.28601f,177.39786f},{-54.191387f,177.39786f},{-40.096764f,177.39786f},{-26.00214f,177.39786f},{-11.907516f,177.39786f},{2.187109f,177.39786f},{16.281734f,177.39786f},{30.37635f,177.39786f},{37.958286f,177.17915f},{45.151405f,176.52303f},{51.955708f,175.42949f},{58.371193f,173.89853f},{64.39786f,171.93015f},{70.035706f,169.52434f},{75.28474f,166.68112f},{80.14495f,163.40048f},{84.61635f,159.68242f},{88.69894f,155.52689f},{92.854416f,150.59377f},{96.57248f,145.51483f},{99.85312f,140.2901f},{102.69634f,134.91956f},{105.10214f,129.40321f},{107.070526f,123.74106f},{108.60149f,117.9331f},{109.69503f,111.97933f},{110.35116f,105.87976f},{110.56989f,99.63441f},{110.56989f,99.63441f}}};
	int coordsSel = 1;
	@Override
	public void settings() {
		size(512, 512);
	}

	public void setup() {
		 xy = new XYscope(this, "", 192000);
	}

	@Override
	public void draw() {
		background(0);

		// clear waves like refreshing background
		xy.clearWaves();
		pushMatrix();
		translate(width/2, height/2);
		coordsSel = 0;
		xy.beginShape();
		for(int i=0;i<coords[coordsSel].length;i++){
			xy.vertex(coords[coordsSel][i][0], coords[coordsSel][i][1]);
		}
		xy.endShape();
		popMatrix();
		// xy.line(mouseX, mouseY, width/4, width/4);
		// xy.point(mouseX, mouseY);

		// build audio from shapes
		xy.buildWaves();

		// draw all analytics
		xy.drawAll();

		// or specific ones
		// xy.drawPath();
		// xy.drawWaveform();
		// xy.drawWaves();
		// xy.drawXY();
	}

	public static void main(String[] args) {
		PApplet.main(new String[] { ATest.class.getName() });
	}
}