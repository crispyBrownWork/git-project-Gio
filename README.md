# git-project-Gio

## stage()

    The stage method takes a given filepath, takes all of the file's children, recursively creates blobs of each child, and logs all staged objects in the index file.

    One caveat of the current implementation is that each log in the index file saves only file names and not relative paths.

## commit()

    The commit method takes its author and message strings, then generates a file in git/objects. The file contains the author, message, current time, hash of the current working directory, and the hash of the previous commit.

### bugs

    A bug that has been fixed is the incompatibility of stage() when given a single, non directory file.
    