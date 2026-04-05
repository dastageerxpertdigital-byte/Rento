package com.dastageer.rento.presentation.shared.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.dp

/**
 * All 46 custom icons for the RentO design system.
 * Each icon is a custom ImageVector — no Material Icons substitution.
 * strokeLinecap = Round, strokeLinejoin = Round, default strokeWidth = 1.8f.
 * Check/Ok icons use strokeWidth = 2.5f.
 */
object RentoIcons {

    private fun buildIcon(
        name: String,
        block: ImageVector.Builder.() -> Unit,
    ): ImageVector = ImageVector.Builder(
        name = "RentoIcons.$name",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply(block).build()

    // Helper: stroke-only path
    private fun ImageVector.Builder.sp(
        d: String,
        sw: Float = 1.8f,
    ) {
        addPath(
            pathData = PathParser().parsePathString(d).toNodes(),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = sw,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }

    // Helper: fill-only path
    private fun ImageVector.Builder.fp(d: String) {
        addPath(
            pathData = PathParser().parsePathString(d).toNodes(),
            fill = SolidColor(Color.Black),
        )
    }

    // Helper: fill+stroke path
    private fun ImageVector.Builder.fsp(d: String, sw: Float = 1.8f) {
        addPath(
            pathData = PathParser().parsePathString(d).toNodes(),
            fill = SolidColor(Color.Black),
            stroke = SolidColor(Color.Black),
            strokeLineWidth = sw,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        )
    }

    // ─── Navigation & Core ──────────────────────────────────────────────

    val Home: ImageVector by lazy {
        buildIcon("Home") { sp("M3,9l9,-7 9,7v11a2,2 0 0,1 -2,2H5a2,2 0 0,1 -2,-2z") }
    }
    val House: ImageVector get() = Home

    val Building: ImageVector by lazy {
        buildIcon("Building") {
            sp("M4,2h16a2,2 0 0,1 2,2v16a2,2 0 0,1 -2,2H4a2,2 0 0,1 -2,-2V4a2,2 0 0,1 2,-2z")
            sp("M9,22v-4h6v4")
            listOf(9f to 7f, 15f to 7f, 9f to 11f, 15f to 11f, 9f to 15f, 15f to 15f).forEach { (cx, cy) ->
                fp("M$cx,${cy}m-1,0a1,1 0 1,0 2,0a1,1 0 1,0 -2,0")
            }
        }
    }

    val Clock: ImageVector by lazy {
        buildIcon("Clock") {
            sp("M12,2A10,10 0 1,0 12,22A10,10 0 0,0 12,2z")
            sp("M12,6L12,12L16,14")
        }
    }

    val Door: ImageVector by lazy {
        buildIcon("Door") {
            sp("M18,20V4a2,2 0 0,0 -2,-2H8a2,2 0 0,0 -2,2v16")
            sp("M2,20L22,20")
            sp("M14,12L14.01,12", sw = 3f)
        }
    }

    // ─── Amenities ──────────────────────────────────────────────────────

    val Wifi: ImageVector by lazy {
        buildIcon("Wifi") {
            sp("M5,12.55a11,11 0 0,1 14.08,0")
            sp("M1.42,9a16,16 0 0,1 21.16,0")
            sp("M8.53,16.11a6,6 0 0,1 6.95,0")
            sp("M12,20L12.01,20", sw = 3f)
        }
    }

    val Snow: ImageVector by lazy {
        buildIcon("Snow") {
            sp("M2,12L22,12")
            sp("M12,2L12,22")
            sp("M20,16L12,12L20,8")
            sp("M4,8L12,12L4,16")
            sp("M16,4L12,12L8,4")
            sp("M8,20L12,12L16,20")
        }
    }

    val Zap: ImageVector by lazy {
        buildIcon("Zap") { sp("M13,2L3,14L12,14L11,22L21,10L12,10Z") }
    }

    val Drop: ImageVector by lazy {
        buildIcon("Drop") { sp("M12,2.69l5.66,5.66a8,8 0 1,1 -11.31,0Z") }
    }

    val Flame: ImageVector by lazy {
        buildIcon("Flame") {
            sp(
                "M8.5,14.5A2.5,2.5 0 0,0 11,12c0,-1.38 -0.5,-2 -1,-3C8.928,6.857 9.776,4.946 12,3c0.5,2.5 2,4.9 4,6.5c2,1.6 3,3.5 3,5.5a7,7 0 1,1 -14,0c0,-1.153 0.433,-2.294 1,-3a2.5,2.5 0 0,0 2.5,2.5z"
            )
        }
    }

    val Parking: ImageVector by lazy {
        buildIcon("Parking") {
            sp("M3,3h18a2,2 0 0,1 2,2v14a2,2 0 0,1 -2,2H3a2,2 0 0,1 -2,-2V5a2,2 0 0,1 2,-2z")
            sp("M9,17V7h4a3,3 0 0,1 0,6H9")
        }
    }

    val Lift: ImageVector by lazy {
        buildIcon("Lift") {
            sp("M4,4h16a2,2 0 0,1 2,2v12a2,2 0 0,1 -2,2H4a2,2 0 0,1 -2,-2V6a2,2 0 0,1 2,-2z")
            sp("M16,10L12,6L8,10")
            sp("M12,18L12,6")
        }
    }

    val Leaf: ImageVector by lazy {
        buildIcon("Leaf") {
            sp("M11,20A7,7 0 0,1 9.8,6.1C15.5,5 17,4.48 19,2c1,2 2,4.18 2,8c0,5.5 -4.78,10 -10,10Z")
            sp("M2,22L11,20")
        }
    }

    val Cctv: ImageVector by lazy {
        buildIcon("Cctv") {
            sp("M23,7L16,12L23,17Z")
            sp("M1,5h15a2,2 0 0,1 2,2v10a2,2 0 0,1 -2,2H1a2,2 0 0,1 -2,-2V7a2,2 0 0,1 2,-2z")
        }
    }

    val Wash: ImageVector by lazy {
        buildIcon("Wash") {
            sp("M4,2h16a2,2 0 0,1 2,2v16a2,2 0 0,1 -2,2H4a2,2 0 0,1 -2,-2V4a2,2 0 0,1 2,-2z")
            sp("M12,10A4,4 0 1,0 12,18A4,4 0 0,0 12,10z")
            sp("M4,6L20,6")
        }
    }

    val Paw: ImageVector by lazy {
        buildIcon("Paw") {
            sp("M12,12c-2.76,0 -5,2.24 -5,5s2.24,5 5,5s5,-2.24 5,-5s-2.24,-5 -5,-5z")
            listOf(6f to 10f, 18f to 10f).forEach { (cx, cy) ->
                sp("M$cx,${cy - 3}A3,3 0 1,0 $cx,${cy + 3}A3,3 0 0,0 $cx,${cy - 3}")
            }
            listOf(10.5f to 5f, 13.5f to 5f).forEach { (cx, cy) ->
                sp("M$cx,${cy - 3}A3,3 0 1,0 $cx,${cy + 3}A3,3 0 0,0 $cx,${cy - 3}")
            }
        }
    }

    // ─── Nearby Places ──────────────────────────────────────────────────

    val Hospital: ImageVector by lazy {
        buildIcon("Hospital") {
            sp("M3,3h18a2,2 0 0,1 2,2v14a2,2 0 0,1 -2,2H3a2,2 0 0,1 -2,-2V5a2,2 0 0,1 2,-2z")
            sp("M12,8L12,16")
            sp("M8,12L16,12")
        }
    }

    val Cart: ImageVector by lazy {
        buildIcon("Cart") {
            fp("M9,20A1,1 0 1,0 9,22A1,1 0 0,0 9,20z")
            fp("M20,20A1,1 0 1,0 20,22A1,1 0 0,0 20,20z")
            sp("M1,1h4l2.68,13.39a2,2 0 0,0 2,1.61h9.72a2,2 0 0,0 2,-1.61L23,6H6")
        }
    }

    val School: ImageVector by lazy {
        buildIcon("School") {
            sp("M4,19.5A2.5,2.5 0 0,1 6.5,17H20")
            sp("M6.5,2H20v20H6.5A2.5,2.5 0 0,1 4,19.5v-15A2.5,2.5 0 0,1 6.5,2z")
        }
    }

    val Bank: ImageVector by lazy {
        buildIcon("Bank") {
            sp("M3,22L21,22")
            listOf(6f, 10f, 14f, 18f).forEach { x -> sp("M$x,18L$x,11") }
            sp("M12,2L20,7L4,7Z")
        }
    }

    // ─── Rating & Status ────────────────────────────────────────────────

    val Star: ImageVector by lazy {
        buildIcon("Star") {
            sp("M12,2L15.09,8.26L22,9.27L17,14.14L18.18,21.02L12,17.77L5.82,21.02L7,14.14L2,9.27L8.91,8.26Z")
        }
    }

    val Check: ImageVector by lazy {
        buildIcon("Check") { sp("M20,6L9,17L4,12", sw = 2.5f) }
    }
    val Ok: ImageVector get() = Check

    val Close: ImageVector by lazy {
        buildIcon("Close") {
            sp("M18,6L6,18")
            sp("M6,6L18,18")
        }
    }
    val X: ImageVector get() = Close

    // ─── People ─────────────────────────────────────────────────────────

    val Users: ImageVector by lazy {
        buildIcon("Users") {
            sp("M17,21v-2a4,4 0 0,0 -4,-4H5a4,4 0 0,0 -4,4v2")
            sp("M9,3A4,4 0 1,0 9,11A4,4 0 0,0 9,3z")
            sp("M23,21v-2a4,4 0 0,0 -3,-3.87")
            sp("M16,3.13a4,4 0 0,1 0,7.75")
        }
    }

    val User: ImageVector by lazy {
        buildIcon("User") {
            sp("M20,21v-2a4,4 0 0,0 -4,-4H8a4,4 0 0,0 -4,4v2")
            sp("M12,3A4,4 0 1,0 12,11A4,4 0 0,0 12,3z")
        }
    }

    // ─── Navigation ─────────────────────────────────────────────────────

    val MapIcon: ImageVector by lazy {
        buildIcon("Map") {
            sp("M1,6L1,22L8,18L16,22L23,18L23,2L16,6L8,2Z")
            sp("M8,2L8,18")
            sp("M16,6L16,22")
        }
    }

    val Chat: ImageVector by lazy {
        buildIcon("Chat") { sp("M21,15a2,2 0 0,1 -2,2H7l-4,4V5a2,2 0 0,1 2,-2h14a2,2 0 0,1 2,2z") }
    }

    val Heart: ImageVector by lazy {
        buildIcon("Heart") {
            sp(
                "M20.84,4.61a5.5,5.5 0 0,0 -7.78,0L12,5.67l-1.06,-1.06a5.5,5.5 0 0,0 -7.78,7.78l1.06,1.06L12,21.23l7.78,-7.78l1.06,-1.06a5.5,5.5 0 0,0 0,-7.78z"
            )
        }
    }

    val HeartFilled: ImageVector by lazy {
        buildIcon("HeartFilled") {
            fsp(
                "M20.84,4.61a5.5,5.5 0 0,0 -7.78,0L12,5.67l-1.06,-1.06a5.5,5.5 0 0,0 -7.78,7.78l1.06,1.06L12,21.23l7.78,-7.78l1.06,-1.06a5.5,5.5 0 0,0 0,-7.78z"
            )
        }
    }

    val Bell: ImageVector by lazy {
        buildIcon("Bell") {
            sp("M18,8A6,6 0 0,0 6,8c0,7 -3,9 -3,9h18s-3,-2 -3,-9")
            sp("M13.73,21a2,2 0 0,1 -3.46,0")
        }
    }

    val Search: ImageVector by lazy {
        buildIcon("Search") {
            sp("M11,3A8,8 0 1,0 11,19A8,8 0 0,0 11,3z")
            sp("M21,21L16.65,16.65")
        }
    }

    val Back: ImageVector by lazy {
        buildIcon("Back") {
            sp("M19,12H5")
            sp("M12,19L5,12L12,5")
        }
    }

    val Plus: ImageVector by lazy {
        buildIcon("Plus") {
            sp("M12,5L12,19")
            sp("M5,12L19,12")
        }
    }

    val Filter: ImageVector by lazy {
        buildIcon("Filter") {
            sp("M4,6L20,6")
            sp("M8,12L16,12")
            sp("M11,18L13,18")
        }
    }

    val Pin: ImageVector by lazy {
        buildIcon("Pin") {
            sp("M21,10c0,7 -9,13 -9,13s-9,-6 -9,-13a9,9 0 0,1 18,0z")
            sp("M12,7A3,3 0 1,0 12,13A3,3 0 0,0 12,7z")
        }
    }

    val Share: ImageVector by lazy {
        buildIcon("Share") {
            sp("M18,2A3,3 0 1,0 18,8A3,3 0 0,0 18,2z")
            sp("M6,9A3,3 0 1,0 6,15A3,3 0 0,0 6,9z")
            sp("M18,16A3,3 0 1,0 18,22A3,3 0 0,0 18,16z")
            sp("M8.59,13.51L15.42,17.49")
            sp("M15.41,6.51L8.59,10.49")
        }
    }

    // ─── Property Details ───────────────────────────────────────────────

    val Bed: ImageVector by lazy {
        buildIcon("Bed") {
            sp("M2,4L2,20")
            sp("M2,8h20v12H2")
            sp("M2,8c0,-2.2 1.8,-4 4,-4h12c2.2,0 4,1.8 4,4")
        }
    }

    val Bath: ImageVector by lazy {
        buildIcon("Bath") {
            sp("M4,12h16a1,1 0 0,1 1,1v3a4,4 0 0,1 -4,4H7a4,4 0 0,1 -4,-4v-3a1,1 0 0,1 1,-1z")
            sp("M4,12V6a2,2 0 0,1 2,-2h0a2,2 0 0,1 2,2v0.5")
        }
    }

    // ─── Communication ──────────────────────────────────────────────────

    val Send: ImageVector by lazy {
        buildIcon("Send") {
            sp("M22,2L11,13")
            sp("M22,2L15,22L11,13L2,9Z")
        }
    }

    val Camera: ImageVector by lazy {
        buildIcon("Camera") {
            sp("M23,19a2,2 0 0,1 -2,2H3a2,2 0 0,1 -2,-2V8a2,2 0 0,1 2,-2h4l2,-3h6l2,3h4a2,2 0 0,1 2,2z")
            sp("M12,9A4,4 0 1,0 12,17A4,4 0 0,0 12,9z")
        }
    }

    // ─── UI Controls ────────────────────────────────────────────────────

    val Chevron: ImageVector by lazy {
        buildIcon("Chevron") { sp("M9,18L15,12L9,6") }
    }

    val Grid: ImageVector by lazy {
        buildIcon("Grid") {
            listOf(3f to 3f, 14f to 3f, 14f to 14f, 3f to 14f).forEach { (x, y) ->
                sp("M$x,${y}h7v7h-7Z")
            }
        }
    }

    val ListIcon: ImageVector by lazy {
        buildIcon("List") {
            listOf(6f, 12f, 18f).forEach { y ->
                sp("M8,${y}L21,$y")
                sp("M3,${y}L3.01,$y", sw = 3f)
            }
        }
    }

    val Eye: ImageVector by lazy {
        buildIcon("Eye") {
            sp("M1,12s4,-8 11,-8s11,8 11,8s-4,8 -11,8s-11,-8 -11,-8z")
            sp("M12,9A3,3 0 1,0 12,15A3,3 0 0,0 12,9z")
        }
    }

    // ─── Saved & Documents ──────────────────────────────────────────────

    val Bookmark: ImageVector by lazy {
        buildIcon("Bookmark") { sp("M19,21L12,16L5,21V5a2,2 0 0,1 2,-2h10a2,2 0 0,1 2,2z") }
    }

    val BookmarkFilled: ImageVector by lazy {
        buildIcon("BookmarkFilled") { fsp("M19,21L12,16L5,21V5a2,2 0 0,1 2,-2h10a2,2 0 0,1 2,2z") }
    }

    val FileText: ImageVector by lazy {
        buildIcon("FileText") {
            sp("M14,2H6a2,2 0 0,0 -2,2v16a2,2 0 0,0 2,2h12a2,2 0 0,0 2,-2V8z")
            sp("M14,2L14,8L20,8")
            sp("M16,13L8,13")
            sp("M16,17L8,17")
            sp("M10,9L9,9L8,9")
        }
    }

    // ─── Security & Settings ────────────────────────────────────────────

    val Shield: ImageVector by lazy {
        buildIcon("Shield") { sp("M12,22s8,-4 8,-10V5l-8,-3l-8,3v7c0,6 8,10 8,10z") }
    }

    val Lightbulb: ImageVector by lazy {
        buildIcon("Lightbulb") {
            sp("M9,18h6")
            sp("M10,22h4")
            sp(
                "M15.09,14c0.18,-0.98 0.65,-1.74 1.41,-2.5A6,6 0 1,0 6,8c0,1 0.25,2 0.75,2.8c0.76,0.76 1.23,1.52 1.41,2.5"
            )
        }
    }

    val PackageIcon: ImageVector by lazy {
        buildIcon("Package") {
            sp("M16.5,9.4L7.5,4.21")
            sp(
                "M21,16V8a2,2 0 0,0 -1,-1.73l-7,-4a2,2 0 0,0 -2,0l-7,4A2,2 0 0,0 3,8v8a2,2 0 0,0 1,1.73l7,4a2,2 0 0,0 2,0l7,-4A2,2 0 0,0 21,16z"
            )
            sp("M3.27,6.96L12,12.01L20.73,6.96")
            sp("M12,22.08L12,12")
        }
    }

    // ─── Theme Modes ────────────────────────────────────────────────────

    val Sun: ImageVector by lazy {
        buildIcon("Sun") {
            sp("M12,7A5,5 0 1,0 12,17A5,5 0 0,0 12,7z")
            listOf(
                "M12,1L12,3",
                "M12,21L12,23",
                "M4.22,4.22L5.64,5.64",
                "M18.36,18.36L19.78,19.78",
                "M1,12L3,12",
                "M21,12L23,12",
                "M4.22,19.78L5.64,18.36",
                "M18.36,5.64L19.78,4.22"
            ).forEach { sp(it) }
        }
    }

    val Moon: ImageVector by lazy {
        buildIcon("Moon") { sp("M21,12.79A9,9 0 1,1 11.21,3A7,7 0 0,0 21,12.79z") }
    }

    // ─── Auth & Account ─────────────────────────────────────────────────

    val Mail: ImageVector by lazy {
        buildIcon("Mail") {
            sp("M4,4h16c1.1,0 2,0.9 2,2v12c0,1.1 -0.9,2 -2,2H4c-1.1,0 -2,-0.9 -2,-2V6c0,-1.1 0.9,-2 2,-2z")
            sp("M22,6L12,13L2,6")
        }
    }

    val Lock: ImageVector by lazy {
        buildIcon("Lock") {
            sp("M3,11h18a2,2 0 0,1 2,2v7a2,2 0 0,1 -2,2H3a2,2 0 0,1 -2,-2v-7a2,2 0 0,1 2,-2z")
            sp("M7,11V7a5,5 0 0,1 10,0v4")
        }
    }

    val LogOut: ImageVector by lazy {
        buildIcon("LogOut") {
            sp("M9,21H5a2,2 0 0,1 -2,-2V5a2,2 0 0,1 2,-2h4")
            sp("M16,17L21,12L16,7")
            sp("M21,12L9,12")
        }
    }
}
