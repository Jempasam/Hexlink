package jempasam.hexlink.network

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketSender
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import net.minecraft.network.PacketByteBuf

object ClientSendDataPacket {
    init{
        ClientPlayNetworking.registerGlobalReceiver(SendDataPacket.DATASYNC_ID){ client: MinecraftClient, handler: ClientPlayNetworkHandler, packet: PacketByteBuf, packetSender: PacketSender ->
            if(!MinecraftClient.getInstance().isIntegratedServerRunning) SendDataPacket.receiveData(packet)
        }
    }
}