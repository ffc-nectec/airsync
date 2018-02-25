package th.in.ffc.airsync.api.services;

import th.in.ffc.module.struct.Pcu;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("/pcu")
public class PcuService {

    static private Hashtable<String,Pcu> pcuMap=new Hashtable<String,Pcu>();

    class PcuList{
        List<Pcu> pcu=new ArrayList<Pcu>();
        public PcuList(List<Pcu> pcu) {
            this.pcu = pcu;
        }
        public List<Pcu> getPcu() {
            return pcu;
        }
    }


    @GET
    public Response getPcu(@Context HttpServletRequest req, @DefaultValue("false") @QueryParam("mypcu") boolean mypcu){
        List<Pcu> pcu=new ArrayList<Pcu>();
        String out = "Pcu Register getRemoteAddr="+req.getRemoteAddr()+" getRemoteHost="+req.getRemoteHost()+" ";
        System.out.println(out);

        if(mypcu) {
            //pcu.add(new Pcu("ipr",req.getRemoteAddr()+"  "+req.getRemoteHost()));
            Pcu pcutemp=pcuMap.get(req.getRemoteAddr());

            if(pcutemp!=null)pcu.add(pcutemp);

        }else {
            //pcu.add(new Pcu("Nectec", "0093kjsdfsadfdasf"));
            //pcu.add(new Pcu("Nectec02", "0093soijflksjdfdsaf"));
            pcuMap.forEach((s, pcu1) -> {
                pcu.add(pcu1);
            });
        }
        if(pcu.size()<1)throw new NotFoundException();
        return Response.status(Response.Status.OK).entity(new PcuList(pcu)).build();
    }



    @POST
    public Response postPcu(@Context HttpServletRequest req,Pcu pcu){

        //pcuMap.put(req.getRemoteAddr(),new Pcu("neccccc","ccccc","aaaaa"));
        pcuMap.put(req.getRemoteAddr(),pcu);


        String out = "Pcu Register getRemoteAddr="+req.getRemoteAddr()+" getRemoteHost="+req.getRemoteHost()+" ";
        System.out.println(out);

        return Response.status(Response.Status.OK).build();
    }



}
