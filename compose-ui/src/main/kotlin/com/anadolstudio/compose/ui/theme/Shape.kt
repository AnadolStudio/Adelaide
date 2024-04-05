package com.anadolstudio.compose.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.anadolstudio.compose.ui.view.text.Text

val Shapes: Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(16.dp),
)

val Shapes.largeIcon: CornerBasedShape
    get() = RoundedCornerShape(24.dp)

val Shapes.largeBanner: CornerBasedShape
    get() = RoundedCornerShape(20.dp)

val Shapes.largeBlock: CornerBasedShape
    get() = RoundedCornerShape(16.dp)

val Shapes.largeShimmer: CornerBasedShape
    get() = RoundedCornerShape(12.dp)

val Shapes.tiny: CornerBasedShape
    get() = RoundedCornerShape(10.dp)

val Shapes.micro: CornerBasedShape
    get() = RoundedCornerShape(4.dp)

val Shapes.textShimmer: CornerBasedShape
    get() = RoundedCornerShape(2.dp)

val Shapes.image: CornerBasedShape
    get() = RoundedCornerShape(8.dp)

@Preview(showBackground = true)
@Composable
private fun ShapesPreview() {
    Column {
        Row {
            ShapePreview(Shapes.small, name = "small")
            ShapePreview(Shapes.medium, name = "medium")
            ShapePreview(Shapes.large, name = "large")
            ShapePreview(Shapes.tiny, name = "tiny")
        }
    }
}

@Composable
private fun ShapePreview(shape: Shape, name: String) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .padding(8.dp)
            .clip(shape)
            .background(Color.White)
            .border(1.dp, Color.Gray, shape)
            .padding(10.dp)
    ) {
        Text(name)
    }
}
