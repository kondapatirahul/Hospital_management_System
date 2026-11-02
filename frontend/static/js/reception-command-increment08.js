(function(){
"use strict";
const button=document.getElementById("cf-reception-refresh"),status=document.getElementById("cf-reception-status");
const domains=document.getElementById("cf-reception-domains"),insights=document.getElementById("cf-reception-insights"),count=document.getElementById("cf-reception-count");
if(!button||!status||!domains||!insights)return;
const esc=v=>String(v??"").replace(/[&<>"']/g,c=>({"&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#39;"}[c]));
async function refresh(){
 button.disabled=true;status.textContent="Refreshing receptionist workspace…";
 try{
  const response=await fetch("/receptionist/command-center/summary",{headers:{"Accept":"application/json"},credentials:"same-origin"});
  if(!response.ok)throw new Error("Request failed ("+response.status+")");
  const data=await response.json();
  domains.innerHTML=(data.domains||[]).map(d=>`<article><b>${esc(d.domain.replaceAll("_"," "))}</b><span>${d.insights.length} actionable · ${d.metricsAvailable} signals / ${d.rulesEvaluated} controls</span></article>`).join("");
  const items=data.insights||[];count.textContent=items.length+" items";
  insights.innerHTML=items.length?items.slice(0,15).map(x=>`<article class="cf-reception-insight" data-severity="${esc(x.severity)}"><i></i><div><b>${esc(x.description)}</b><p>${esc(x.metric)}: ${esc(x.observedValue)} · threshold ${esc(x.threshold)}</p><small>${esc(x.recommendedAction)}</small></div><em>${esc(x.severity)}</em></article>`).join(""):'<p class="cf-reception-empty">No threshold breaches in connected front-desk signals.</p>';
  status.textContent=`${data.appointmentsToday} appointments · ${data.queueEntriesToday} queue entries · ${data.waitingCount} waiting · ${data.availableDoctors} available doctors · ${data.totalPatients} patients. ${data.metricsAvailable} of ${data.rulesEvaluated} rule checks have connected signals. ${data.dataNotice}`;
 }catch(error){status.textContent="Could not refresh reception insights: "+error.message;}
 finally{button.disabled=false;}
}
button.addEventListener("click",refresh);refresh();
})();
