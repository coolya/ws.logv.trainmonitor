/*
 * Copyright 2013. Kolja Dummann <k.dummann@gmail.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package ws.logv.trainmonitor.data;

/**
 * Created by kolja on 01.06.13.
 */
public final class Tables {
	public static final class Train {
		public static final String NAME = "train";

		public final class ColumnNames {
			public static final String FINISHED = "finished";
			public static final String TRAIN_ID = "trainId";
			public static final String NEXT_RUN = "nextrun";
			public static final String STARTED = "started";
			public static final String STATUS = "status";
			public static final String ID = "id";
		}

		public static final class ColumnIds {
			public static final Integer FINISHED = 0;
			public static final Integer TRAIN_ID = FINISHED + 1;
			public static final Integer NEXT_RUN = TRAIN_ID + 1;
			public static final Integer STARTED = NEXT_RUN + 1;
			public static final Integer STATUS = STARTED + 1;
			public static final Integer ID = STATUS + 1;
		}
	}
}
