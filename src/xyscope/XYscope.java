/*
 * cc ted davis 2017-18
 */

package xyscope;

import processing.core.*;
import static processing.core.PApplet.*;

import java.util.ArrayList;

// minim
import ddf.minim.*;
import ddf.minim.ugens.*;
import javax.sound.sampled.*;

/**
 * Render graphics on a vector display by converting them to audio (oscilloscope
 * X-Y mode, laser).
 *
 */

public class XYscope {

	// myParent is a reference to the parent sketch
	PApplet myParent;

	/**
	 * Collection of current shapes rendered by buildWaves().
	 */

	public XYShapeList shapes = new XYShapeList();

	// minim
	Minim minim, minimZ;

	/**
	 * minim AudioOutput, for customizing audio out.
	 */

	public AudioOutput outXY, outZ;

	/**
	 * minim Summer, for customizing patching filters.
	 */
	public Summer sumXY, sumZ;

	/**
	 * minim Oscil, for customizing of oscillators.
	 */
	public Oscil waveX, waveY, waveZ;

	Wavetable tableX, tableY, tableZ;
	Pan panX = new Pan(-1);
	Pan panY = new Pan(1);

	Mixer.Info[] mixerInfo;

	float initAmp = 1.0f;
	PVector amp = new PVector(initAmp, initAmp, initAmp);
	float initFreq = 50f; // 43.065
	PVector freq = new PVector(initFreq, initFreq, initFreq);

	// Mixing audio channels
	AudioOutput mixXY;
	boolean useMix = false;

	int waveSizeVal = 1024;
	int waveSizeValOG = waveSizeVal;
	float[] shapeY = new float[waveSizeVal];
	float[] shapeX = new float[waveSizeVal];
	float[] shapeZ = new float[waveSizeVal];
	float[] shapePreY = new float[waveSizeVal];
	float[] shapePreX = new float[waveSizeVal];
	float[] shapePreZ = new float[waveSizeVal];
	boolean debugWave = false;
	int debugSize = 10;

	int ellipseDetail = 30;

	boolean useEase = false;
	float easeVal = .1f;
	boolean useZ = false;
	boolean useSmooth = false;
	int smoothVal = 12;

	boolean zaxis = false;
	float zaxisMax = 1f;
	float zaxisMin = -1f;
	int zoffset = 1;

	/**
	 * Initialize library in setup(), use default system audio out setting.
	 * 
	 * @param theParent
	 *            PApplet to apply to, typically 'this'
	 */
	// * @example basic_shapes
	public XYscope(PApplet theParent) {
		myParent = theParent;
		welcome();
		initMinim();
		setMixer();
	}

	/**
	 * Initialize instance of XYscope and patch to an already existing signal.
	 * 
	 * @param theParent
	 *            PApplet to apply to, typically 'this'
	 * @param outMix
	 *            AudioOutput to merge instance and of XYscope to
	 */
	public XYscope(PApplet theParent, AudioOutput outMix) {
		myParent = theParent;
		initMinim();
		setWaveTable(outMix);
		useMix = true;
	}

	/**
	 * Initialize library in setup(), custom soundcard by String for XY.
	 * 
	 * @param theParent
	 *            PApplet to apply to, typically 'this'
	 * @param xyMixer
	 *            Name of custom sound mixer to use for XY.
	 */
	public XYscope(PApplet theParent, String xyMixer) {
		myParent = theParent;
		welcome();
		getMixerInfo();
		initMinim();
		setMixer(xyMixer);
	}

	/**
	 * Initialize library in setup(), using default soundcard and set custom
	 * sample rate (44100, 192000).
	 * 
	 * @param theParent
	 *            PApplet to apply to, typically 'this'
	 * @param sampleR
	 *            Sample rate for soundcard (44100, 48000, 192000).
	 */
	public XYscope(PApplet theParent, int sampleR) {
		myParent = theParent;
		welcome();
		getMixerInfo();
		initMinim();
		setMixer(sampleR);
	}

	/**
	 * Initialize library in setup(), custom soundcard by string for XY and set
	 * custom sample rate (44100, 192000).
	 * 
	 * @param theParent
	 *            PApplet to apply to, typically 'this'
	 * @param xyMixer
	 *            Name of custom sound mixer to use for XY.
	 * @param sampleR
	 *            Sample rate for soundcard (44100, 48000, 192000).
	 */
	public XYscope(PApplet theParent, String xyMixer, int sampleR) {
		myParent = theParent;
		welcome();
		getMixerInfo();
		initMinim();
		setMixer(xyMixer, sampleR);
	}

	private void welcome() {
		System.out.println("XYscope 2.0.0 by Ted Davis http://teddavis.org");
	}

	/**
	 * Lists all audio input/output options available
	 */
	public void getMixerInfo() {
		mixerInfo = AudioSystem.getMixerInfo();
		for (int i = 0; i < mixerInfo.length; i++) {
			println(i + " = " + mixerInfo[i].getName());
		}
	}

	private static Mixer getMixerByName(String toFind) {
		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			if (toFind.equals(info.getName())) {
				return AudioSystem.getMixer(info);
			}
		}
		return null;
	}

	private void initMinim() {
		minim = new Minim(myParent);
		minimZ = new Minim(myParent);
	}

	private void setMixer() {
		outXY = minim.getLineOut(Minim.STEREO, waveSizeValOG);
		setWaveTable();
	}

	private void setMixer(int sampleR) {
		outXY = minim.getLineOut(Minim.STEREO, waveSizeValOG, sampleR);
		setWaveTable();
	}

	private void setMixer(String xyMixer) {
		getMixerInfo();
		Mixer mixer = getMixerByName(xyMixer);
		minim.setOutputMixer(mixer);
		outXY = minim.getLineOut(Minim.STEREO, waveSizeValOG);
		setWaveTable();
	}

	private void setMixer(String xyMixer, int sampleR) {
		getMixerInfo();
		Mixer mixer = getMixerByName(xyMixer);
		minim.setOutputMixer(mixer);
		outXY = minim.getLineOut(Minim.STEREO, waveSizeValOG, sampleR);
		setWaveTable();
	}

	private void setWaveTable() {
		sumXY = new Summer();
		sumXY.setChannelCount(2);

		tableX = Waves.randomNHarms(0);
		waveX = new Oscil(freq.x, amp.x, tableX);
		tableX.setWaveform(shapeX);
		waveX.patch(panX).patch(sumXY);

		tableY = Waves.randomNHarms(0);
		waveY = new Oscil(freq.y, amp.y, tableY);
		tableY.setWaveform(shapeY);
		waveY.patch(panY).patch(sumXY);

		waveReset();
		sumXY.patch(outXY);

	}

	private void setWaveTable(AudioOutput outMix) {
		tableX = Waves.randomNHarms(0);
		waveX = new Oscil(freq.x, amp.x, tableX);
		tableX.setWaveform(shapeX);
		waveX.patch(panX).patch(outMix);

		tableY = Waves.randomNHarms(0);
		waveY = new Oscil(freq.y, amp.y, tableY);
		tableY.setWaveform(shapeY);
		waveY.patch(panY).patch(outMix);

		mixXY = outMix;

		waveReset();
	}

	private void setWaveTableZ() {
		if (zaxis) {
			tableZ = Waves.randomNHarms(0);
			waveZ = new Oscil(freq.z, amp.z, tableZ);
			tableZ.setWaveform(shapeZ);
			waveZ.patch(outZ); // need pan?? or gets full amp to both channels?
		}

		waveReset();
	}

	/**
	 * Reset time-step used by XYZ oscillators if they slip when changing
	 * frequencies.
	 * 
	 */

	public void waveReset() {
		waveX.reset();
		waveY.reset();
		if (zaxis)
			waveZ.reset();
	}

	/**
	 * Patch z-axis to custom soundcard by String. Note: Auto z-axis has been
	 * disabled until solved, until then, one can manually buildZ()
	 * 
	 * @param zMixer
	 *            Name of custom sound mixer to use for Z.
	 */
	public void z(String zMixer) {
		Mixer mixerZ = getMixerByName(zMixer);
		minimZ.setOutputMixer(mixerZ);
		outZ = minimZ.getLineOut(Minim.STEREO, waveSizeValOG);
		zaxis = true;
		useZ = true;
		setWaveTableZ();
	}

	/**
	 * Patch z-axis to custom soundcard by String and set custom sample rate
	 * (44100, 192000). Note: Auto z-axis has been disabled until solved, until
	 * then, one can manually buildZ()
	 * 
	 * @param zMixer
	 *            Name of custom sound mixer to use for Z.
	 * 
	 * @param sampleR
	 *            Sample rate for soundcard (44100, 48000, 192000).
	 * 
	 */
	public void z(String zMixer, int sampleR) {
		Mixer mixerZ = getMixerByName(zMixer);
		minimZ.setOutputMixer(mixerZ);
		outZ = minimZ.getLineOut(Minim.STEREO, waveSizeValOG, sampleR);
		zaxis = true;
		useZ = true;
		setWaveTableZ();
	}

	/**
	 * Check if z-axis waveform is being automatically drawn from added shapes.
	 */
	public boolean zAuto() {
		return useZ;
	}

	/**
	 * Enabled by default for automatic generation of z-axis waveform based on
	 * added shapes. Disable if creating your own waveform (used for blanking,
	 * dotted line, etc. Note: Only works if using a second audio output channel
	 * 
	 * @param zAutoBool
	 *            true/false for generating z waveform
	 */
	public void zAuto(boolean zAutoBool) {
		useZ = zAutoBool;
	}

	/**
	 * Get ArrayList of all coordinates used for vector drawing as an ArrayList
	 * of PVector's.
	 * 
	 * @return ArrayList<PVector>
	 */
	public ArrayList<PVector> wavePoints() {
		return shapes.getPoints();
	}

	/**
	 * Get min and max values for z-axis output as two value array.
	 * 
	 * @return array containing [zaxisMin, zaxisMax]
	 */
	public float[] zRange() {
		float[] zRangeFloat = { zaxisMin, zaxisMax };
		return zRangeFloat;
	}

	/**
	 * Set min and max values for z-axis output. Necessary for any inverted
	 * z-axis devices.
	 * <p>
	 * default is zMin: 1, zMax: -1
	 * 
	 * @param zMin
	 *            float between -1 to 1
	 * @param zMax
	 *            float between -1 to 1
	 */
	public void zRange(float zMin, float zMax) {
		zaxisMin = zMin;
		zaxisMax = zMax;
	}

	/**
	 * Get current amplitude setting of XY oscillators.
	 * 
	 * @return float
	 */
	public PVector amp() {
		return amp;
	}

	/**
	 * Set new amplitude for both XYZ oscillators as float.
	 * 
	 * @param newAmp
	 *            value between 0.0 - 1.0
	 */
	public void amp(float newAmp) {
		amp.x = constrain(newAmp, 0f, 1f);
		amp.y = constrain(newAmp, 0f, 1f);
		waveX.setAmplitude(amp.x);
		waveY.setAmplitude(amp.y);
		if (zaxis) {
			amp.z = constrain(newAmp, 0f, 1f);
			waveZ.setAmplitude(amp.z);
		}
	}

	/**
	 * Set new amplitude for both X + Y oscillators as float.
	 * 
	 * @param newAmpX
	 *            value between 0.0 - 1.0
	 * @param newAmpY
	 *            value between 0.0 - 1.0
	 */
	public void amp(float newAmpX, float newAmpY) {
		amp.x = constrain(newAmpX, 0f, 1f);
		amp.y = constrain(newAmpY, 0f, 1f);
		waveX.setAmplitude(amp.x);
		waveY.setAmplitude(amp.y);
	}

	/**
	 * Set new amplitude for both X + Y + Z oscillators as float.
	 * 
	 * @param newAmpX
	 *            value between 0.0 - 1.0
	 * @param newAmpY
	 *            value between 0.0 - 1.0
	 */
	public void amp(float newAmpX, float newAmpY, float newAmpZ) {
		amp.x = constrain(newAmpX, 0f, 1f);
		amp.y = constrain(newAmpY, 0f, 1f);
		waveX.setAmplitude(amp.x);
		waveY.setAmplitude(amp.y);
		if (zaxis) {
			amp.z = constrain(newAmpZ, 0f, 1f);
			waveZ.setAmplitude(amp.z);
		}
	}

	/**
	 * Set new amplitude for each XYZ oscillator separately using a PVector for
	 * the values.
	 * 
	 * @param newAmp
	 *            PVector of values between 0.0 - 1.0
	 */
	public void amp(PVector newAmp) {
		float tempX = constrain(newAmp.x, 0f, 1f);
		float tempY = constrain(newAmp.y, 0f, 1f);
		waveX.setAmplitude(tempX);
		waveY.setAmplitude(tempY);
		if (zaxis) {
			float tempZ = constrain(newAmp.z, 0f, 1f);
			waveZ.setAmplitude(tempZ);
		}
	}

	/**
	 * Get current frequency for X, Y, Z oscillators as a PVector.
	 * 
	 * @return PVector
	 */
	public PVector freq() {
		return freq;
	}

	/**
	 * Set new frequency for all XYZ oscillators together as single float.
	 * 
	 * @param newFreq
	 *            float
	 */
	public void freq(float newFreq) {
		freq = new PVector(newFreq, newFreq, newFreq);
		waveX.setFrequency(freq.x);
		waveY.setFrequency(freq.y);
		if (zaxis)
			waveZ.setFrequency(freq.z);
	}

	/**
	 * Set new frequency for all X + Y oscillators.
	 * 
	 * @param newFreqX
	 *            float
	 * @param newFreqY
	 *            float
	 */
	public void freq(float newFreqX, float newFreqY) {
		freq.x = newFreqX;
		freq.y = newFreqY;
		waveX.setFrequency(freq.x);
		waveY.setFrequency(freq.y);
	}

	/**
	 * Set new frequency for all X + Y oscillators.
	 * 
	 * @param newFreqX
	 *            float
	 * @param newFreqY
	 *            float
	 * @param newFreqZ
	 *            float
	 */
	public void freq(float newFreqX, float newFreqY, float newFreqZ) {
		freq.x = newFreqX;
		freq.y = newFreqY;
		waveX.setFrequency(freq.x);
		waveY.setFrequency(freq.y);
		if (zaxis) {
			freq.z = newFreqZ;
			waveZ.setFrequency(freq.z);
		}
	}

	/**
	 * Set new frequency for each XYZ oscillator separately using a PVector for
	 * the values.
	 * 
	 * @param newFreq
	 *            PVector
	 */
	public void freq(PVector newFreq) {
		freq = newFreq;
		waveX.setFrequency(freq.x);
		waveY.setFrequency(freq.y);
		if (zaxis)
			waveZ.setFrequency(freq.z);
	}

	/**
	 * Enable/Disable easing transitions from one set of buildWaves() to the
	 * next. Default is false.
	 * 
	 * @param easeBool
	 *            true/false
	 */
	public void ease(boolean easeBool) {
		useEase = easeBool;
	}

	/**
	 * Check if easing between each frame of buildWaves() is enabled.
	 * 
	 * @return boolean
	 */
	public boolean ease() {
		return useEase;
	}

	/**
	 * Returns current easeAmount, 0.0 - 1.0.
	 * 
	 * @return float
	 */
	public float easeAmount() {
		return easeVal;
	}

	/**
	 * Set new easing value for speed between buildWave() transitions.
	 * 
	 * @param newEaseValue
	 *            float between 0.0 - 1.0
	 */
	public void easeAmount(float newEaseValue) {
		easeVal = newEaseValue;
	}

	/**
	 * Enable/Disable debug view for comparing waveform to shape.
	 * 
	 * @param easeBool
	 *            true/false
	 */
	public void debugView(boolean debugBool) {
		debugWave = debugBool;
	}

	/**
	 * Check if debugView is active.
	 * 
	 * @return boolean
	 */
	public boolean debugView() {
		return debugWave;
	}

	/**
	 * Get size of wavetables. By default, it's the same as the
	 * outXY.bufferSize()
	 * 
	 * @param newSize
	 *            int
	 */
	public int waveSize() {
		return waveSizeVal;
	}

	/**
	 * Set custom size for wavetables. By default, it's the same as the
	 * outXY.bufferSize()
	 * 
	 * @param newSize
	 *            int
	 */
	public void waveSize(int newSize) {
		waveSizeVal = newSize;
		shapeY = new float[waveSizeVal];
		shapeX = new float[waveSizeVal];
		// shapeZ = new float[waveSizeVal];
		shapePreY = new float[waveSizeVal];
		shapePreX = new float[waveSizeVal];
		// shapePreZ = new float[waveSizeVal];
		tableX.setWaveform(shapeX);
		tableY.setWaveform(shapeY);
		// if (zaxis)
		// tableZ.setWaveform(shapeZ);
	}

	/**
	 * Clears the waveforms from previous buildWaves(). Useful to call at top of
	 * draw(), similar to using background() to clear the slate before building
	 * the waveforms at the bottom of your draw with buildWaves().
	 */
	public void clearWaves() {
		for (int i = 0; i < shapeX.length; i++) {
			shapePreX[i] = 0;
			shapePreY[i] = 0;
		}

		if (zaxis && useZ) {
			for (int i = 0; i < shapeZ.length; i++) {
				shapePreZ[i] = zaxisMin;
			}
		}

		shapes = new XYShapeList();
		currentShape = null;
	}

	/**
	 * Generate the XY oscillator waveforms from all added shapes for sending
	 * audio to vector display. Call this after drawing any primitive shapes.
	 * New Rendering mode in place, if old dots style is preferred, use
	 * buildWaves(-1).
	 * 
	 * @see points()
	 */
	public void buildWaves(int bwm) {
		if (bwm == 0) {
			if (shapes.size() > 0) {
				if (shapes.totalSize() < waveSizeValOG) {
					if (waveSize() != shapes.totalSize()) {
						waveSize(shapes.totalSize());
					}

					int SID = 0;
					if (waveSize() == shapes.totalSize()) {
						for (XYShape shape : shapes) {
							for (int i = 0; i < shape.size(); i++) {
								shapePreX[SID] = map(shape.get(i).x, 0f, 1f, -1f, 1f);
								shapePreY[SID] = map(shape.get(i).y, 0f, 1f, 1f, -1f);
								SID++;
							}
						}
					}
				} else {
					if (waveSize() != waveSizeValOG)
						waveSize(waveSizeValOG);

					ArrayList<PVector> ts = shapes.getPoints();
					for (int i = 0; i < shapeX.length; i++) {
						int ptsSel = (int) Math.floor(map(i, 0f, shapeX.length, 0, ts.size()));
						shapePreX[i] = map(ts.get(ptsSel).x, 0f, 1f, -1f, 1f);
						shapePreY[i] = map(ts.get(ptsSel).y, 0f, 1f, 1f, -1f);
					}
				}
			} else {
				waveSize(1);
				shapePreX[0] = 0f;
				shapePreY[0] = 0f;
			}
		} else if (bwm == -1) {
			if (waveSize() != waveSizeValOG)
				waveSize(waveSizeValOG);
			if (shapes.size() > 0) {
				ArrayList<PVector> ts = shapes.getPoints();
				for (int i = 0; i < shapeX.length; i++) {
					int ptsSel = (int) Math.floor(map(i, 0f, shapeX.length, 0, ts.size()));
					shapePreX[i] = map(ts.get(ptsSel).x, 0f, 1f, -1f, 1f);
					shapePreY[i] = map(ts.get(ptsSel).y, 0f, 1f, 1f, -1f);
				}
			}
		}

		// easing
		if (useEase) {
			easeWaves();
		} else {
			for (int i = 0; i < shapePreX.length; i++) {
				shapeX[i] = shapePreX[i];
				shapeY[i] = shapePreY[i];
				if (zaxis && useZ)
					shapeZ[i] = shapePreZ[i];
			}

			if (zaxis && useZ) {
				for (int i = 0; i < shapePreZ.length; i++) {
					shapeZ[i] = shapePreZ[i];
				}
			}
		}

		// smooth
		if (useSmooth) {
			tableX.smooth(smoothVal);
			tableY.smooth(smoothVal);
		}
	}

	/**
	 * Generate the XY(Z) oscillator waveforms from all added points/shapes for
	 * sending audio to vector display. Call this after drawing any primitive
	 * shapes.
	 * 
	 * @see points()
	 */
	public void buildWaves() {
		buildWaves(0);
	}

	/**
	 * Build custom X oscillator waveform. Use waveSize() to ensure you send the
	 * right number of values.
	 * 
	 * @param newWave
	 *            Array of normalized floats between 0.0 - 1.0
	 * @see waveSize();
	 */
	public void buildX(float[] newWave) {
		for (int i = 0; i < newWave.length; i++) {
			int sel = (int) Math.floor(map(i, 0f, newWave.length, 0f, shapePreX.length));
			shapePreX[i] = map(newWave[sel], 0f, 1f, -1f, 1f);
		}

		if (useEase) {
			easeWaves(shapePreX, shapeX);
		} else {
			for (int i = 0; i < shapePreX.length; i++) {
				shapeX[i] = shapePreX[i];
			}
		}
	}

	/**
	 * Build custom Y oscillator waveform. Use waveSize() to ensure you send the
	 * right number of values.
	 * 
	 * @param newWave
	 *            Array of normalized floats between 0.0 - 1.0
	 * @see waveSize();
	 */
	public void buildY(float[] newWave) {
		for (int i = 0; i < newWave.length; i++) {
			int sel = (int) Math.floor(map(i, 0f, newWave.length, 0f, shapePreY.length));
			shapePreY[i] = map(newWave[sel], 0f, 1f, 1f, -1f);
		}

		if (useEase) {
			easeWaves(shapePreY, shapeY);
		} else {
			for (int i = 0; i < shapePreY.length; i++) {
				shapeY[i] = shapePreY[i];
			}
		}
	}

	/**
	 * Build custom Z oscillator waveform. Use waveSize() to ensure you send the
	 * right number of values.
	 * 
	 * @param newWave
	 *            Array of normalized floats between 0.0 - 1.0
	 * @see waveSize();
	 */
	public void buildZ(float[] newWave) {
		for (int i = 0; i < newWave.length; i++) {
			int sel = (int) Math.floor(map(i, 0f, newWave.length, 0f, shapePreZ.length));
			shapePreZ[i] = map(newWave[sel], 0f, 1f, zaxisMin, zaxisMax);
		}

		if (useEase) {
			easeWaves(shapePreZ, shapeZ);
		} else {
			for (int i = 0; i < shapePreZ.length; i++) {
				shapeZ[i] = shapePreZ[i];
			}
		}
	}

	private void easeWaves(float[] sShape, float[] tShape) {
		for (int i = 0; i < sShape.length; i++) {
			float targetX = sShape[i];
			float dx = targetX - tShape[i];
			tShape[i] += dx * easeVal;
		}
	}

	private void easeWaves() {
		easeWaves(shapePreX, shapeX);
		easeWaves(shapePreY, shapeY);

		if (zaxis && useZ) {
			easeWaves(shapePreZ, shapeZ);

		}
	}

	/**
	 * Check if waveform smoothing is enabled/disabled.
	 * 
	 * @param easeVal
	 *            true/false
	 */
	public boolean smoothWaves() {
		return useSmooth;
	}

	/**
	 * Enable/disable Smooth waveforms to reduce visibility of points in
	 * drawing. Default is false (new rendering doesn't need it).
	 * 
	 * @param smoothWavesBool
	 *            true/false
	 */
	public void smoothWaves(boolean smoothWavesBool) {
		useSmooth = smoothWavesBool;
	}

	/**
	 * Get number of steps for smoothing waveforms.
	 * 
	 * @see <a href=
	 *      "http://code.compartmental.net/minim/wavetable_method_smooth.html">Minim
	 *      -> Wavetable -> smooth()</a>
	 */
	public int smoothWavesAmount() {
		return smoothVal;
	}

	/**
	 * Set number of steps for smoothing waveforms. Default is 12.
	 * 
	 * @param swAmount
	 *            new int value for smoothening waveform
	 * @see <a href=
	 *      "http://code.compartmental.net/minim/wavetable_method_smooth.html">Minim
	 *      -> Wavetable -> smooth()</a>
	 */
	public void smoothWavesAmount(int swAmount) {
		smoothVal = swAmount;
	}

	/**
	 * Draw all information
	 * <ul>
	 * <li>drawPath()
	 * <li>drawWaveform()
	 * <li>drawWave()
	 * <li>drawXY()
	 * <li>drawPoints()
	 * </ul>
	 */
	public void drawAll() {
		drawPath();
		drawWaveform();
		drawWave();
		drawXY();
		drawPoints();
	}

	/**
	 * Draw path of points remapped from normalized values to width + height of
	 * sketch.
	 */
	public void drawPath() {
		myParent.pushStyle();
		myParent.noFill();
		myParent.stroke(255);
		myParent.pushMatrix();
		myParent.beginShape();
		for (XYShape shape : shapes) {
			for (int i = 0; i < shape.size(); i++) {
				float x = map(shape.get(i).x, 0f, 1f, 0f, myParent.width);
				float y = map(shape.get(i).y, 0f, 1f, 0f, myParent.height);
				myParent.vertex(x, y);
			}
		}
		myParent.endShape(OPEN);
		myParent.popMatrix();
		myParent.popStyle();
	}

	/**
	 * Draw points (as 3px ellipses) remapped from normalized values to width +
	 * height of sketch.
	 */
	public void drawPoints() {
		myParent.pushStyle();
		myParent.fill(0, 255, 0);
		myParent.noStroke();
		myParent.pushMatrix();
		for (XYShape shape : shapes) {
			for (int i = 0; i < shape.size(); i++) {
				float x = map(shape.get(i).x, 0f, 1f, 0f, myParent.width);
				float y = map(shape.get(i).y, 0f, 1f, 0f, myParent.height);
				myParent.ellipse(x, y, 3, 3);
			}
		}
		myParent.popMatrix();
		myParent.noFill();
		myParent.popStyle();
	}

	/**
	 * Simulate X-Y mode of oscilloscope output.
	 */
	public void drawXY() {
		myParent.pushStyle();
		myParent.noFill();
		myParent.stroke(50, 255, 50);
		myParent.pushMatrix();
		myParent.translate(myParent.width / 2, myParent.height / 2);
		myParent.beginShape();
		AudioOutput tempXY;
		if (useMix) {
			tempXY = mixXY;
		} else {
			tempXY = outXY;
		}

		for (int i = 0; i < tempXY.bufferSize() - 1; i++) {
			float lAudio = tempXY.left.get(i) * (float) myParent.width / 2;
			float rAudio = tempXY.right.get(i) * (float) myParent.height / 2;
			myParent.curveVertex(lAudio, rAudio * -1f);
		}

		myParent.endShape();

		if (debugWave) {
			float mouseT = (myParent.mouseX / (float) myParent.width);
			float mx = tableX.value(mouseT) * (float) myParent.width / 2 * amp.x;
			float my = -tableY.value(mouseT) * (float) myParent.height / 2 * amp.y;
			myParent.pushStyle();
			myParent.fill(50, 255, 50);
			myParent.noStroke();
			myParent.ellipse(mx, my, debugSize, debugSize);
			myParent.popStyle();
		}

		myParent.popMatrix();
		myParent.popStyle();
	}

	/**
	 * Draw waveform of all XYZ oscillators.
	 * <ul>
	 * <li>Red: X
	 * <li>Blue: Y
	 * <li>Green: Z
	 */
	public void drawWaveform() {
		myParent.pushStyle();
		myParent.noFill();
		myParent.pushMatrix();
		myParent.beginShape();

		// X -> L
		myParent.stroke(50, 50, 255);
		for (int i = 0; i < myParent.width; i++) {
			myParent.vertex(i, (float) myParent.height * .25f
					- ((float) myParent.height * .125f) * tableX.value((float) i / (float) myParent.width));
		}
		myParent.endShape();

		// Y -> R
		myParent.stroke(255, 50, 50);
		myParent.beginShape();
		for (int i = 0; i < myParent.width; i++) {
			myParent.vertex(i, (float) myParent.height * .75f
					- ((float) myParent.height * 0.125f) * tableY.value((float) i / (float) myParent.width));
		}
		myParent.endShape();

		if (debugWave) {
			float mouseT = (myParent.mouseX / (float) myParent.width);
			float lx = myParent.mouseX;
			float ly = (float) myParent.height * .25f - ((float) myParent.height * .125f)
					* tableX.value((float) myParent.mouseX / (float) myParent.width);
			float ry = (float) myParent.height * .75f - ((float) myParent.height * .125f)
					* tableY.value((float) myParent.mouseX / (float) myParent.width);
			myParent.pushStyle();
			myParent.noStroke();
			myParent.fill(50, 50, 255);
			myParent.ellipse(lx - 2, ly, debugSize, debugSize);
			myParent.fill(255, 50, 50);
			myParent.ellipse(lx - 2, ry, debugSize, debugSize);
			myParent.popStyle();
		}

		// Z
		if (zaxis) {
			myParent.stroke(50, 255, 50);
			myParent.beginShape();
			for (int i = 0; i < shapeZ.length; i++) {
				float xpos = map(i, 0f, (float) shapeZ.length, 0f, (float) myParent.width);
				myParent.vertex(xpos, (float) myParent.height * .5f - ((float) myParent.height * 0.125f) * shapeZ[i]);// tableZ.value(i));
			}
			myParent.endShape();
		}
		myParent.popMatrix();
		myParent.popStyle();
	}

	/**
	 * Draw wave of all XYZ oscillators.
	 */
	public void drawWave() {
		myParent.pushStyle();
		myParent.stroke(255);
		myParent.noFill();
		myParent.pushMatrix();
		myParent.beginShape();

		AudioOutput tempXY;
		if (useMix) {
			tempXY = mixXY;
		} else {
			tempXY = outXY;
		}

		for (int i = 0; i < tempXY.bufferSize() - 1; i++) {
			float xAudio = map(i, 0, tempXY.bufferSize(), 0, myParent.width);
			float lAudio = tempXY.left.get(i);
			// curveVertex(lAudio, rAudio*-1);
			myParent.vertex(xAudio, myParent.height * .25f - (myParent.height * .25f) * lAudio);
		}
		myParent.endShape();

		myParent.beginShape();
		for (int i = 0; i < tempXY.bufferSize() - 1; i++) {
			float xAudio = map(i, 0, tempXY.bufferSize(), 0, myParent.width);
			float rAudio = tempXY.right.get(i);
			// curveVertex(lAudio, rAudio*-1);
			myParent.vertex(xAudio, myParent.height * .75f + (myParent.height * .25f) * rAudio);
		}
		myParent.endShape();

		if (zaxis) {
			myParent.beginShape();
			for (int i = 0; i < outZ.bufferSize() - 1; i++) {
				float xAudio = map(i, 0, outZ.bufferSize(), 0, myParent.width);
				float rAudio = outZ.right.get(i);
				// curveVertex(lAudio, rAudio*-1);
				myParent.vertex(xAudio, myParent.height * .5f - (myParent.height * .25f) * rAudio);
			}
			myParent.endShape();

		}
		myParent.popMatrix();
		myParent.popStyle();
	}

	int rectM;

	/**
	 * Get detail (number of points) for drawing an ellipse.
	 * 
	 * @return int
	 */
	public int ellipseDetail() {
		return ellipseDetail;
	}

	/**
	 * Set detail (number of points) for drawing an ellipse.
	 * 
	 * @param newED
	 *            int
	 */
	public void ellipseDetail(int newED) {
		ellipseDetail = newED;
	}

	/**
	 * Set rectMode (similar to Processing function).
	 * 
	 * @param rectModeVal
	 *            CORNER or CENTER
	 * @see <a href="https://processing.org/reference/rectMode_.html">Processing
	 *      Reference -> rectMode()</a>
	 */
	public void rectMode(int rectModeVal) {
		if (rectModeVal == 0) {
			rectM = 0;
		} else if (rectModeVal == 3) {
			rectM = 3;
		}
	}

	/**
	 * Draw rectangle, expects rect(x, y, w, h).
	 * 
	 * @see <a href="https://processing.org/reference/rect_.html">Processing
	 *      Reference -> rect()</a>
	 */
	public void rect(float x1, float y1, float w1, float h1) {
		if (rectM == 3) {
			x1 -= w1 / 2;
			y1 -= h1 / 2;
		}
		vertexRect(x1, y1, w1, h1);
	}

	private void vertexRect(float x1, float y1, float w1, float h1) {
		beginShape();
		vertex(x1, y1);
		vertex(x1 + w1, y1);
		vertex(x1 + w1, y1 + h1);
		vertex(x1, y1 + h1);
		vertex(x1, y1);
		if (zaxis && useZ)
			vertex(x1, y1);
		endShape();
	}

	/**
	 * Draw ellipse, expects ellipse(x, y, w, h).
	 * 
	 * @see <a href="https://processing.org/reference/ellipse_.html">Processing
	 *      Reference -> ellipse()</a>
	 */
	public void ellipse(float x1, float y1, float w1, float h1) {
		vertexEllipse(x1, y1, w1, h1);
	}

	// vertexEllipse!
	// based on
	// http://stackoverflow.com/questions/5886628/effecient-way-to-draw-ellipse-with-opengl-or-d3d
	private void vertexEllipse(float cx, float cy, float rx, float ry) {
		float theta = 2f * PI / (float) ellipseDetail;
		float c = cos(theta);// precalculate the sine and cosine
		float s = sin(theta);
		float t;

		float x = 1f;// we start at angle = 0
		float y = 0f;

		beginShape();
		for (int ii = 0; ii < ellipseDetail + 1; ii++) {
			// apply radius and offset
			vertex(x * rx + cx, y * ry + cy);// output vertex
			// apply the rotation matrix
			t = x;
			x = c * x - s * y;
			y = s * t + c * y;

		}
		endShape();
	}

	/**
	 * Draw point, expects point(x, y).
	 * 
	 * @see <a href="https://processing.org/reference/point_.html">Processing
	 *      Reference -> point()</a>
	 */
	public void point(float x1, float y1) {
		beginShape();
		vertex(x1, y1);
		if (zaxis && useZ)
			vertex(x1, y1);
		endShape();
	}

	/**
	 * Draw point, expects point(x, y, z).
	 * 
	 * @see <a href="https://processing.org/reference/point_.html">Processing
	 *      Reference -> point()</a>
	 */
	public void point(float x1, float y1, float z1) {
		beginShape();
		vertex(x1, y1, z1);
		if (zaxis && useZ)
			vertex(x1, y1, z1);
		endShape();
	}

	/**
	 * Draw line, expects line(x1, y1, x2, y2).
	 * 
	 * @see <a href="https://processing.org/reference/line_.html">Processing
	 *      Reference -> line()</a>
	 */
	public void line(float x1, float y1, float x2, float y2) {
		beginShape();
		vertex(x1, y1);
		vertex(x2, y2);
		if (zaxis && useZ)
			vertex(x1, y1);
		endShape();
	}

	/**
	 * Begin multi-vertex shape.
	 * 
	 * @see <a href=
	 *      "https://processing.org/reference/beginShape_.html">Processing
	 *      Reference -> beginShape()</a>
	 */
	public void beginShape() {
		currentShape = new XYShape();
		shapes.add(currentShape);
	}

	/**
	 * Currently sent as normal vertex (to be fixed). Simply here for code ->
	 * vectorcode compatibility.
	 * 
	 * @see <a href=
	 *      "https://processing.org/reference/curveVertex_.html">Processing
	 *      Reference -> curveVertex()</a>
	 */
	public void curveVertex(float x1, float y1) {
		vertex(x1, y1);
	}

	/**
	 * Currently sent as normal vertex (to be fixed). Simply here for code ->
	 * vectorcode compatibility.
	 * 
	 * @see <a href=
	 *      "https://processing.org/reference/curveVertex_.html">Processing
	 *      Reference -> curveVertex()</a>
	 */
	public void curveVertex(float x1, float y1, float z1) {
		vertex(x1, y1, z1);
	}

	/**
	 * Add vertex to complex shape. Expects vertex(x, y).
	 * 
	 * @see <a href="https://processing.org/reference/vertex_.html">Processing
	 *      Reference -> vertex()</a>
	 */
	public void vertex(float x, float y) {
		float x1out = norm(myParent.screenX(x, y), 0f, myParent.width + 0f);
		float y1out = norm(myParent.screenY(x, y), 0f, myParent.height + 0f);
		vertex(new PVector(x1out, y1out));
	}

	/**
	 * Add vertex to complex shape. Expects vertex(x, y, z).
	 * 
	 * @see <a href="https://processing.org/reference/vertex_.html">Processing
	 *      Reference -> vertex()</a>
	 */
	public void vertex(float x, float y, float z) {
		float x1out = norm(myParent.screenX(x, y, z), 0f, myParent.width + 0f);
		float y1out = norm(myParent.screenY(x, y, z), 0f, myParent.height + 0f);
		float z1out = norm(myParent.screenY(x, y, z), 0f, myParent.height + 0f);
		vertex(new PVector(x1out, y1out, z1out));
	}

	/**
	 * Add vertex to complex shape. Expects vertex(PVector()).
	 * 
	 * @see <a href="https://processing.org/reference/vertex_.html">Processing
	 *      Reference -> vertex()</a>
	 */

	public void vertex(PVector p) {
		currentShape.add(p);
	}

	/**
	 * End complex shape.
	 * 
	 * @see <a href="https://processing.org/reference/endShape_.html">Processing
	 *      Reference -> endShape()</a>
	 */

	public void endShape() {
		// not necessary in current setup. maybe useful later for z-axis
	}

	private XYShape currentShape = null;

	public class XYShapeList extends ArrayList<XYShape> {
		public float getDistance() {
			float sum = 0;
			for (XYShape shape : this) {
				sum += shape.getDistance();
			}
			return sum;
		}

		public int totalSize() {
			int tsCounter = 0;
			for (XYShape shape : this) {
				for (int i = 0; i < shape.size(); i++) {
					tsCounter++;
				}
			}
			return tsCounter;
		}

		public ArrayList<PVector> getPoints() {
			ArrayList<PVector> gp = new ArrayList<PVector>();
			for (XYShape shape : this) {
				for (int i = 0; i < shape.size(); i++) {
					gp.add(shape.get(i));
				}
			}
			return gp;
		}
	}

	public class XYShape extends ArrayList<PVector> {
		public float getDistance() {
			float sum = 0;
			for (int i = 0; i < size() - 1; i++) {
				sum += get(i).dist(get(i + 1));
			}
			return sum;
		}
	}
}
