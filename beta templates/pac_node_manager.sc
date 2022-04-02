//------------------------------------------------
//--- 010 Editor v12.0.1 Script File
//
//      File: 
//   Authors: 
//   Version: 
//   Purpose: 
//  Category: 
//   History: 
//------------------------------------------------
BigEndian();
local u32 i;
local u32 nodeindex;
local u32 nodeaddress<format=hex>;
local u32 nodesize;
local u32 writeaddress<format=hex> = FileSize();
local u32 whatdo = InputRadioButtonBox("Node Manager","",0,"Add Node","Delete Node","Save Node","Add Boss");

void ErrorNodeIndex(u32 nodeindex){
    MessageBox( idCancel,
    "Error",
    "Node[%i] does not exist.", nodeindex);
    Exit(0);
};

switch(whatdo){
    case 0://add node//complete
        local u32 pacfileindex = GetFileNum();
        local string nodefile = InputOpenFileName(
            "",
            "All files (*)",
            "" );
        FileOpen(nodefile,false,"",false);
        Assert(ReadUInt(0)==1195464960,"Cannot read node");
        local u32 nodeid = ReadInt(4);
        local u16 gaysize1 = ReadShort(8);
        local u16 gaysize2 = ReadShort(10);
        local u32 rggsize = ReadInt(12);
        local uchar nodedata[gaysize1+gaysize2]<format=hex>;
        ReadBytes(nodedata, 16, gaysize1+gaysize2);
        FileSelect(pacfileindex);
        for(i=0;i<pac.NodeCount;i++){
            local struct Repointadd{
                pac.pointers[i].Data1Pointer+=16;
                pac.pointers[i].Data2Pointer+=16;
            }repointadd;
        };

        local u32 insertaddress<format=hex> = startof(pac.pointers.Data1Pointer) + 12;
        InsertBytes(insertaddress,16,0);
        WriteInt(insertaddress,nodeid);
        WriteInt(insertaddress+4,writeaddress);
        WriteInt(insertaddress+8,writeaddress+gaysize1);
        WriteInt(insertaddress+12,rggsize);
        WriteBytes(nodedata,writeaddress,gaysize1+gaysize2);
        pac.NodeCount++;
        break;

    case 1: //delete node//complete
        nodeindex = InputNumber("Delete Node","Node Index:","");
        if(nodeindex==pac.NodeCount-1){
            nodesize = FileSize() - pac.pointers[nodeindex].Data1Pointer;
        }
        else if(nodeindex<0 || nodeindex>=pac.NodeCount){
            ErrorNodeIndex(nodeindex);
        }
        else{
            nodesize = pac.pointers[nodeindex+1].Data1Pointer - pac.pointers[nodeindex].Data1Pointer;
        };
        local u32 deleteheaderaddress = startof(pac.pointers[nodeindex].Data1Pointer) - 4;
        for(i=0;i<pac.NodeCount;i++){
            local struct Repointheader{
                pac.pointers[i].Data1Pointer-= 16;
                pac.pointers[i].Data2Pointer-= 16;
            }repointheader;
        };
        for(i=nodeindex+1;i<pac.NodeCount;i++){
            local struct Repointdel{
                pac.pointers[i].Data1Pointer-=nodesize;
                pac.pointers[i].Data2Pointer-=nodesize;
            }repointdel;
        };
        DeleteBytes(pac.pointers[nodeindex].Data1Pointer,nodesize);
        DeleteBytes(deleteheaderaddress,16);
        pac.NodeCount--;
        break;

    case 2://save node//complete
        nodeindex = InputNumber("Save Node","Node Index:","");
        nodeaddress = pac.pointers[nodeindex].Data1Pointer;
        nodesize = pac.pointers[nodeindex+1].Data1Pointer - pac.pointers[nodeindex].Data1Pointer;
        local uchar nodedata[nodesize]<format=hex>;
        ReadBytes(nodedata, nodeaddress, nodesize);
        local u32 nodeidwrite<format=hex> = ReadInt(startof(pac.pointers[nodeindex].Data1Pointer) - 4);
        local u32 rgg1size = pac.pointers[nodeindex].Data1Size;
        local u32 rgg2size = pac.pointers[nodeindex].Data2Size;
        local u32 gay1size = pac.pointers[nodeindex].Data2Pointer - pac.pointers[nodeindex].Data1Pointer;
        local u32 gay2size = nodesize - gay1size;
        local u32 savenode = FileNew("Hex", true);
        InsertBytes(0,4,0);
        WriteString(0,"GAY");
        WriteInt(4,nodeidwrite);
        WriteShort(8,gay1size);
        WriteShort(10,gay2size);
        WriteShort(12,rgg1size);
        WriteShort(14,rgg2size);
        WriteBytes(nodedata,16,nodesize);
        break;

    case 3://add boss//need done
            MessageBox( idCancel,
                "Error",
                "This option is not available yet");
            Exit(0);
        break;
    default:
        break;
};
