/*
 * Copyright (c) 2019 Hemanth Savarala.
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by
 *  the Free Software Foundation either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

/*All thanks to Shuttle
* https://github.com/timusus/Shuttle/blob/dev/app/src/main/java/com/simplecity/amp_library/androidauto/AndroidAutoHelper.kt
* */
package code.name.monkey.retromusic.androidauto


sealed class MediaIdWrapper {

    object RootDirectory : MediaIdWrapper()

    object ArtistDirectory : MediaIdWrapper()

    class AlbumDirectory(var artistHash: Int?) : MediaIdWrapper()

    class SongDirectory(var artistHash: String?, var albumId: Long?) : MediaIdWrapper()

    object PlaylistDirectory : MediaIdWrapper()

    object GenreDirectory : MediaIdWrapper()

    class Song(var artistHash: String?, var albumId: Long?, var songId: Long?) : MediaIdWrapper()

    class Genre(var genreId: Long) : MediaIdWrapper()

    class Playlist(var playlistId: Long) : MediaIdWrapper()
}