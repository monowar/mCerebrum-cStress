package org.md2k.cstress;

import android.util.Log;

import md2k.mCerebrum.CSVDataPoint;
import md2k.mCerebrum.cStress.DataPointInterface;
import md2k.mCerebrum.cStress.StreamProcessor;
import md2k.mCerebrum.cStress.library.Time;
import md2k.mCerebrum.cStress.library.structs.DataPoint;
import md2k.mCerebrum.cStress.library.structs.DataPointArray;

/**
 * Copyright (c) 2015, The University of Memphis, MD2K Center
 * - Syed Monowar Hossain <monowar.hossain@gmail.com>
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <p/>
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * <p/>
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
public class StreamProcessorWrapper {
    private static final String TAG = StreamProcessorWrapper.class.getSimpleName();
    StreamProcessor streamProcessor;
    int windowSize = 60000;
    long windowStartTime = -1;

    public StreamProcessorWrapper() {
        streamProcessor = new StreamProcessor(windowSize);

        streamProcessor.dpInterface = new DataPointInterface() {
            @Override
            public void dataPointHandler(String stream, DataPoint dp) {
                System.out.println(stream + " " + dp);
            }

            @Override
            public void dataPointArrayHandler(String stream, DataPointArray dp) {
                System.out.println(stream + " " + dp);
            }
        };

        streamProcessor.registerCallbackDataArrayStream("org.md2k.cstress.fv");
        streamProcessor.registerCallbackDataStream("org.md2k.cstress.data.accel.activity");
    }

    void addDataPoint(CSVDataPoint ap) {
        DataPoint dp = new DataPoint(ap.timestamp, ap.value);
        streamProcessor.add(ap.channel, dp);
        if (windowStartTime < 0)
            windowStartTime = Time.nextEpochTimestamp(dp.timestamp, windowSize);

        if ((dp.timestamp - windowStartTime) >= windowSize) { //Process the buffer every windowSize milliseconds
            long starttime = System.currentTimeMillis();
            streamProcessor.go();
            long endtime = System.currentTimeMillis();
            Log.d(TAG, "Loop iteration in seconds: " + (endtime - starttime) / 1000.0);

            System.out.println("Loop iteration in seconds: " + (endtime - starttime) / 1000.0);
            windowStartTime = Time.nextEpochTimestamp(dp.timestamp, windowSize);
        }
    }
}
