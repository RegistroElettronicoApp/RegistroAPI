export default function NavItem({page} : {page: UiPage}) {
  let active = true
  return (
    <li className="nav-item">
      <a className={"nav-link " + (page.disabled ? "disabled " : "") + (active ? "active" : "")}
         aria-disabled={page.disabled} aria-current={active ? "page" : "false"} href={page.href}>{page.title}</a>
    </li>
  )
}