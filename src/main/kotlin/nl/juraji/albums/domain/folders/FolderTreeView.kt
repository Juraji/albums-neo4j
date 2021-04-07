package nl.juraji.albums.domain.folders

data class FolderTreeView(
    val id: String,
    val name: String,
    val children: List<FolderTreeView>,
    val isRoot: Boolean,
)
