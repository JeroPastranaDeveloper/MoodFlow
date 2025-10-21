package com.jero.designsystem.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jero.core.designsystem.R
import com.jero.designsystem.theme.MoodFlowColors

@Composable
fun MoodFlowButton(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 20.sp,
    textStyle: TextStyle = LocalTextStyle.current,
    fontWeight: FontWeight = FontWeight.Normal,
    leadingIconRes: Int? = null,
    trailingIconRes: Int? = null,
    textColor: Color = Color.Black,
    backgroundColor: Color? = null,
    cornerRadius: Dp = 12.dp,
    buttonHeight: Dp = 56.dp,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(buttonHeight),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor ?: Color.White,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp),
        shape = RoundedCornerShape(cornerRadius),
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            leadingIconRes?.let {
                Image(
                    modifier = Modifier.align(Alignment.CenterStart),
                    painter = painterResource(it),
                    contentDescription = "Button leading icon",
                )
            }

            Text(
                modifier = Modifier.align(Alignment.Center),
                text = text,
                color = textColor,
                fontSize = fontSize,
                fontWeight = fontWeight,
                style = textStyle,
                maxLines = 1,
            )

            trailingIconRes?.let {
                Image(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    painter = painterResource(it),
                    contentDescription = "Button trailing icon",
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MoodFlowButtonPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MoodFlowButton(text = "Button 1") {}
        MoodFlowButton(
            text = "Button 2",
            textColor = Color.Red,
            textStyle = TextStyle(fontStyle = FontStyle.Italic),
            fontWeight = FontWeight.Bold,
            leadingIconRes = R.drawable.ic_google_logo,
        ) {}
        MoodFlowButton(
            text = "Button 2",
            textColor = Color.Red,
            textStyle = TextStyle(fontStyle = FontStyle.Italic),
            fontWeight = FontWeight.Bold,
            trailingIconRes = R.drawable.ic_google_logo,
        ) {}
        MoodFlowButton(
            text = "Button 2",
            textColor = Color.White,
            leadingIconRes = R.drawable.ic_google_logo,
            trailingIconRes = R.drawable.ic_google_logo,
            backgroundColor = MoodFlowColors.defaultLightColors().pastelBlue,
        ) {}
    }
}
