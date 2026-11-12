package com.hamada.offline

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.UUID

private val Ink = Color(0xFF080D18)
private val Panel = Color(0xFF151F32)
private val Cyan = Color(0xFF65DDF5)
private val Muted = Color(0xFF91A2B9)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("hamada_profile", Context.MODE_PRIVATE)
        val id = prefs.getString("hamada_id", null) ?: ("HAM-" + UUID.randomUUID().toString().replace("-", "").take(8).uppercase()).also {
            prefs.edit().putString("hamada_id", it).apply()
        }
        setContent { HamadaApp(id) }
    }
}

@Composable
fun HamadaApp(myId: String) {
    var tab by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    var draft by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(listOf<String>()) }
    MaterialTheme(colorScheme = darkColorScheme(
        background = Ink, surface = Panel, primary = Cyan,
        onBackground = Color.White, onSurface = Color.White
    )) {
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF101D32), Ink, Color(0xFF080B13))))) {
            Column(Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(horizontal = 20.dp)) {
                Row(Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 22.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("HAMADA", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                        Text("OFFLINE MESSENGER", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
                    }
                    Box(Modifier.clip(CircleShape).background(Color(0x223ED7B0)).padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(7.dp).background(Color(0xFF45D6A2), CircleShape))
                            Spacer(Modifier.width(7.dp))
                            Text("OFFLINE MODE", color = Color(0xFF83E6C3), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                GlassCard(Modifier.fillMaxWidth()) {
                    Text("YOUR HAMADA ID", color = Muted, fontSize = 10.sp, letterSpacing = 1.8.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(myId, Modifier.weight(1f), color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        IconButton(onClick = { }) { Icon(Icons.Default.ContentCopy, "Copy ID", tint = Cyan) }
                    }
                    Text("معرّفك المحلي — لا يحتاج رقم هاتف", color = Muted, fontSize = 12.sp)
                }
                Spacer(Modifier.height(20.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(when(tab) { 0 -> "المحادثات"; 1 -> "الأجهزة القريبة"; 2 -> "الشبكة"; else -> "الإعدادات" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.Default.WifiOff, contentDescription = "Offline", tint = Muted)
                }
                Spacer(Modifier.height(12.dp))
                when(tab) {
                    0 -> {
                        OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("ابحث بـ HAMADA ID") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(18.dp))
                        Spacer(Modifier.height(14.dp))
                        if (messages.isEmpty()) EmptyState("لسه مفيش محادثات", "ابدأ بإضافة HAMADA ID. الاتصال المباشر قيد التطوير.")
                        else LazyColumn(Modifier.weight(1f)) { items(messages.size) { i -> Text(messages[i], Modifier.fillMaxWidth().padding(vertical = 8.dp), color = Color.White) } }
                        Spacer(Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(value = draft, onValueChange = { draft = it }, modifier = Modifier.weight(1f), placeholder = { Text("اكتب رسالة محلية...") }, shape = RoundedCornerShape(20.dp), singleLine = true)
                            IconButton(onClick = { if (draft.isNotBlank()) { messages = messages + draft; draft = "" } }) { Icon(Icons.Default.Send, "Add local draft", tint = Cyan) }
                        }
                        Text("تنبيه: الرسائل هنا تجريبية ومحلية فقط، لم يتم تفعيل الإرسال اللاسلكي بعد.", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
                    }
                    1 -> EmptyState("مفيش أجهزة مكتشفة", "اكتشاف Bluetooth LE هيُفعّل في المرحلة التالية.")
                    2 -> EmptyState("شبكة HAMADA", "مفيش عقد Relay متصلة حالياً.")
                    else -> EmptyState("إعدادات التطبيق", "الخصوصية • المعرف • حالة الاتصال")
                }
                Spacer(Modifier.weight(1f, fill = false))
                Row(Modifier.fillMaxWidth().padding(vertical = 12.dp).clip(RoundedCornerShape(24.dp)).background(Color(0xCC18243A)).border(1.dp, Color(0x334FD8F4), RoundedCornerShape(24.dp)).padding(6.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    listOf(Icons.Default.ChatBubble, Icons.Default.Bluetooth, Icons.Default.Hub, Icons.Default.Settings).forEachIndexed { index, icon ->
                        Column(Modifier.weight(1f).clip(RoundedCornerShape(18.dp)).clickable { tab = index }.background(if(tab == index) Color(0x334FD8F4) else Color.Transparent).padding(vertical = 9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(icon, null, tint = if(tab == index) Cyan else Muted)
                            Text(listOf("Chats", "Nearby", "Network", "Settings")[index], color = if(tab == index) Cyan else Muted, fontSize = 9.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GlassCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.clip(RoundedCornerShape(25.dp)).background(Brush.linearGradient(listOf(Color(0xCC263A56), Color(0xAA111C2E)))).border(1.dp, Color(0x5569DDF4), RoundedCornerShape(25.dp)).padding(18.dp), content = content)
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(top = 45.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.size(76.dp).clip(CircleShape).background(Color(0x222FCAE8)), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.Sensors, null, tint = Cyan, modifier = Modifier.size(34.dp))
        }
        Spacer(Modifier.height(16.dp))
        Text(title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(6.dp))
        Text(subtitle, color = Muted, fontSize = 12.sp)
    }
}
