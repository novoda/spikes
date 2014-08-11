package spike.palette.paul.com.palettespike;

/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class ColorScheme {

    public final int primaryAccent;
    public final int secondaryAccent;
    public final int tertiaryAccent;

    public final int primaryText;
    public final int secondaryText;

    public ColorScheme(int primaryAccent, int secondaryAccent, int tertiaryAccent,
                       int primaryText, int secondaryText) {
        this.primaryAccent = primaryAccent;
        this.secondaryAccent = secondaryAccent;
        this.tertiaryAccent = tertiaryAccent;
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
    }
}