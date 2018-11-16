/*
 * MIT License
 *
 * Copyright (c) 2018 Matthew Hogan <matt@matthogan.co.uk>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package co.uk.matthogan.jonfig;

import lombok.AllArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.channels.Channels;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Basic JSON configuration parser for Java</p>
 *
 * @author Matthew Hogan
 */
@AllArgsConstructor
public class Jonfig {

    private File base;
    private Class clazz;
    private Logger logger;

    {
        this.base = new File("");
    }

    public <T> T load(String file, GenericLoad<T> genericLoad) {
        File path = new File(this.base.getPath() + file);

        this.copyDefaults(path, this.getResource(file));

        try {
            this.logger.info("Loading config: " + this.base + file);
            return genericLoad.load(new JSONParser().parse(new FileReader(path)));

        } catch (IOException | ParseException exception) {
            this.logger.log(Level.SEVERE, "Failed to load config: " + file);
            exception.printStackTrace();
        }

        return null;
    }

    private InputStream getResource(String filename) {
        return this.clazz.getResourceAsStream(filename);
    }

    private void copyDefaults(File file, InputStream defaults) {
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }

            if (!this.isEmpty(file)) {
                return;
            }

            new FileOutputStream(file).getChannel().transferFrom(
                    Channels.newChannel(defaults),
                    0, defaults.available()
            );
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private boolean isEmpty(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            return bufferedReader.readLine() == null;
        } catch (IOException ignore) {
            return true;
        }
    }

    public interface GenericLoad<T> {
        T load(Object json);
    }
}