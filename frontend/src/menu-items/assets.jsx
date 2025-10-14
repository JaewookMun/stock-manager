// ==============================|| MENU ITEMS - ASSETS ||============================== //

const assets = {
  id: "assets",
  title: "ASSETS",
  type: "group",
  children: [
    {
      id: "realized-pnl",
      title: "실현 손익",
      type: "item",
      icon: <i className="ph ph-coins"></i>,
      url: "/assets/realized-pnl"
    },
    {
      id: "cash-flow",
      title: "입출금 내역",
      type: "item",
      icon: <i className="ph ph-money-wavy"></i>,
      url: "/assets/cash-flow"
    }
  ]
}

export default assets